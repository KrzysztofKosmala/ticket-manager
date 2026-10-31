package pl.ticket.knowledge.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import pl.ticket.knowledge.document.KnowledgeDocumentLoader;
import pl.ticket.knowledge.document.KnowledgeMetadata;
import pl.ticket.knowledge.model.KnowledgeIngestionResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class KnowledgeIngestionServiceTest {

    @Test
    void shouldStoreNewIngestionBeforeDeletingPreviousOne() {
        KnowledgeDocumentLoader loader = mock(KnowledgeDocumentLoader.class);
        TokenTextSplitter splitter = new TokenTextSplitter();
        VectorStore vectorStore = mock(VectorStore.class);
        Document document = new Document("Treść testowego dokumentu", Map.of(
                KnowledgeMetadata.SOURCE, "test.md",
                KnowledgeMetadata.DOCUMENT_TYPE, "KNOWLEDGE"
        ));
        when(loader.load()).thenReturn(List.of(document));

        KnowledgeIngestionService service = new KnowledgeIngestionService(loader, splitter, vectorStore);

        KnowledgeIngestionResult result = service.ingest();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> chunksCaptor = ArgumentCaptor.forClass(List.class);
        InOrder order = inOrder(vectorStore);
        order.verify(vectorStore).add(chunksCaptor.capture());
        order.verify(vectorStore).delete(any(org.springframework.ai.vectorstore.filter.Filter.Expression.class));

        Document storedChunk = chunksCaptor.getValue().get(0);
        assertThat(storedChunk.getMetadata())
                .containsEntry(KnowledgeMetadata.KNOWLEDGE_BASE, "ticket-manager")
                .containsEntry(KnowledgeMetadata.INGESTION_ID, result.ingestionId())
                .containsEntry("parent_document_id", document.getId())
                .containsEntry("chunk_index", 0)
                .containsEntry("total_chunks", 1)
                .containsEntry(KnowledgeMetadata.SOURCE, "test.md")
                .doesNotContainKey("chunkIndex");
        assertThat(document.getMetadata()).doesNotContainKey(KnowledgeMetadata.INGESTION_ID);
        assertThat(result.loadedDocumentCount()).isEqualTo(1);
        assertThat(result.chunkCount()).isEqualTo(1);
    }

    @Test
    void shouldKeepPreviousIngestionWhenNoDocumentsAreLoaded() {
        KnowledgeDocumentLoader loader = mock(KnowledgeDocumentLoader.class);
        VectorStore vectorStore = mock(VectorStore.class);
        when(loader.load()).thenReturn(List.of());
        KnowledgeIngestionService service = new KnowledgeIngestionService(
                loader, new TokenTextSplitter(), vectorStore);

        assertThatThrownBy(service::ingest)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No knowledge chunks");

        verifyNoInteractions(vectorStore);
    }

    @Test
    void shouldKeepPreviousIngestionWhenSplitterDropsAllText() {
        KnowledgeDocumentLoader loader = mock(KnowledgeDocumentLoader.class);
        VectorStore vectorStore = mock(VectorStore.class);
        when(loader.load()).thenReturn(List.of(new Document("")));
        KnowledgeIngestionService service = new KnowledgeIngestionService(
                loader, new TokenTextSplitter(), vectorStore);

        assertThatThrownBy(service::ingest).isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(vectorStore);
    }

    @Test
    void shouldNotDeletePreviousIngestionWhenAddingDocumentsFails() {
        KnowledgeDocumentLoader loader = mock(KnowledgeDocumentLoader.class);
        VectorStore vectorStore = mock(VectorStore.class);
        when(loader.load()).thenReturn(List.of(new Document("Payment status reference.")));
        doThrow(new IllegalStateException("Embedding model unavailable"))
                .when(vectorStore).add(any());
        KnowledgeIngestionService service = new KnowledgeIngestionService(
                loader, new TokenTextSplitter(), vectorStore);

        assertThatThrownBy(service::ingest).hasMessage("Embedding model unavailable");

        verify(vectorStore, never()).delete(any(org.springframework.ai.vectorstore.filter.Filter.Expression.class));
    }
}
