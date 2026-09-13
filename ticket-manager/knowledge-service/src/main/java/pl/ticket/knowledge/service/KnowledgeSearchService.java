package pl.ticket.knowledge.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import pl.ticket.knowledge.document.KnowledgeMetadata;
import pl.ticket.dto.KnowledgeSearchResult;

import java.util.List;

@Service
public class KnowledgeSearchService {

    private final VectorStore vectorStore;

    public KnowledgeSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<KnowledgeSearchResult> search(String query, int topK) {
        FilterExpressionBuilder filter = new FilterExpressionBuilder();
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .filterExpression(filter.eq(
                        KnowledgeMetadata.KNOWLEDGE_BASE,
                        KnowledgeMetadata.KNOWLEDGE_BASE_NAME
                ).build())
                .build();

        // PgVectorStore embeds the query and searches PostgreSQL using the same embedding model.
        List<Document> documents = vectorStore.similaritySearch(request);

        return documents.stream()
                .map(document -> new KnowledgeSearchResult(
                        document.getText(),
                        (String) document.getMetadata().get(KnowledgeMetadata.SOURCE),
                        document.getScore()
                ))
                .toList();
    }
}
