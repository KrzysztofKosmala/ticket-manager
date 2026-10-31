package pl.ticket.knowledge.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import pl.ticket.dto.KnowledgeSearchResult;
import pl.ticket.knowledge.document.KnowledgeMetadata;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KnowledgeSearchServiceTest {

    private final VectorStore vectorStore = mock(VectorStore.class);
    private final KnowledgeSearchService service = new KnowledgeSearchService(vectorStore);

    @Test
    void shouldSearchOnlyOurKnowledgeBaseAndPreserveSourcesAndRanking() {
        Document first = Document.builder()
                .text("PAID means the payment was accepted.")
                .metadata(Map.of(KnowledgeMetadata.SOURCE, "statuses.md"))
                .score(0.9)
                .build();
        Document second = Document.builder()
                .text("An order has a separate status.")
                .metadata(Map.of(KnowledgeMetadata.SOURCE, "orders.md"))
                .score(0.7)
                .build();
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenAnswer(invocation -> {
            SearchRequest request = invocation.getArgument(0);
            assertThat(request.getQuery()).isEqualTo("What does PAID mean?");
            assertThat(request.getTopK()).isEqualTo(2);
            assertThat(request.getFilterExpression()).isEqualTo(new FilterExpressionBuilder()
                    .eq(KnowledgeMetadata.KNOWLEDGE_BASE, KnowledgeMetadata.KNOWLEDGE_BASE_NAME).build());
            return List.of(first, second);
        });

        assertThat(service.search("What does PAID mean?", 2)).containsExactly(
                new KnowledgeSearchResult(first.getText(), "statuses.md", 0.9),
                new KnowledgeSearchResult(second.getText(), "orders.md", 0.7)
        );
    }

    @Test
    void shouldReturnAnEmptyListWhenNothingWasFound() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        assertThat(service.search("Unknown topic", 3)).isEmpty();
    }

    @Test
    void shouldPropagateInfrastructureFailureInsteadOfPretendingThereAreNoMatches() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenThrow(new IllegalStateException("Embedding model unavailable"));

        assertThatThrownBy(() -> service.search("Payment status", 3))
                .hasMessage("Embedding model unavailable");
    }
}
