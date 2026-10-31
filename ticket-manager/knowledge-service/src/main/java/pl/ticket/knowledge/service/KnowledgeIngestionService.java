package pl.ticket.knowledge.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ticket.knowledge.document.KnowledgeDocumentLoader;
import pl.ticket.knowledge.document.KnowledgeMetadata;
import pl.ticket.knowledge.model.KnowledgeIngestionResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KnowledgeIngestionService {

    private final KnowledgeDocumentLoader documentLoader;
    private final TokenTextSplitter textSplitter;
    private final VectorStore vectorStore;

    public KnowledgeIngestionService(KnowledgeDocumentLoader documentLoader,
                                     TokenTextSplitter textSplitter,
                                     VectorStore vectorStore) {
        this.documentLoader = documentLoader;
        this.textSplitter = textSplitter;
        this.vectorStore = vectorStore;
    }

    @Transactional
    public KnowledgeIngestionResult ingest() {
        String ingestionId = UUID.randomUUID().toString();
        List<Document> sourceDocuments = documentLoader.load();
        List<Document> splitDocuments = textSplitter.split(sourceDocuments);
        if (splitDocuments.isEmpty()) {
            throw new IllegalStateException("No knowledge chunks found. Previous ingestion has been kept.");
        }
        List<Document> chunks = addIngestionMetadata(splitDocuments, ingestionId);

        // Both JDBC operations commit together; a failure rolls back the database changes.
        vectorStore.add(chunks);
        deletePreviousIngestion(ingestionId);

        return new KnowledgeIngestionResult(ingestionId, sourceDocuments.size(), chunks.size());
    }

    private List<Document> addIngestionMetadata(List<Document> documents, String ingestionId) {
        List<Document> chunks = new ArrayList<>(documents.size());

        for (Document document : documents) {
            Map<String, Object> metadata = new HashMap<>(document.getMetadata());
            metadata.put(KnowledgeMetadata.KNOWLEDGE_BASE, KnowledgeMetadata.KNOWLEDGE_BASE_NAME);
            metadata.put(KnowledgeMetadata.INGESTION_ID, ingestionId);

            chunks.add(new Document(document.getId(), document.getText(), metadata));
        }
        return chunks;
    }

    private void deletePreviousIngestion(String currentIngestionId) {
        FilterExpressionBuilder filter = new FilterExpressionBuilder();
        vectorStore.delete(filter.and(
                filter.eq(KnowledgeMetadata.KNOWLEDGE_BASE, KnowledgeMetadata.KNOWLEDGE_BASE_NAME),
                filter.ne(KnowledgeMetadata.INGESTION_ID, currentIngestionId)
        ).build());
    }
}
