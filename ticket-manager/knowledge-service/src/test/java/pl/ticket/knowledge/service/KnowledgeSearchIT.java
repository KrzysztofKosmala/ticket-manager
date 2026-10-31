package pl.ticket.knowledge.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.ticket.knowledge.document.KnowledgeMetadata;
import pl.ticket.dto.KnowledgeSearchResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "knowledge.ingestion.on-startup=false"
)
@Transactional
class KnowledgeSearchIT {

    @Autowired
    private KnowledgeIngestionService ingestionService;

    @Autowired
    private KnowledgeSearchService searchService;

    @Autowired
    private VectorStore vectorStore;

    @ParameterizedTest
    @CsvSource({
            "Co oznacza status platnosci PAID?, order-and-payment-statuses.md",
            "Jak wyszukiwac wydarzenia i terminy?, event-search-and-tickets.md",
            "Czy istnieje polityka zwrotu biletow?, support-boundaries.md"
    })
    void shouldRetrieveExpectedSourceFromOurMarkdowns(String query, String expectedSource) {
        ingestionService.ingest();

        List<KnowledgeSearchResult> results = searchService.search(query, 3);

        assertThat(results).isNotEmpty().hasSizeLessThanOrEqualTo(3);
        assertThat(results).extracting(KnowledgeSearchResult::source).contains(expectedSource);
        assertThat(results).allSatisfy(result -> {
            assertThat(result.text()).isNotBlank();
            assertThat(result.score()).isNotNull();
        });
    }

    @Test
    void shouldExcludeOtherKnowledgeBasesEvenWhenTheirTextMatchesExactly() {
        ingestionService.ingest();
        String query = "Payment accepted for the order.";
        vectorStore.add(List.of(new Document(query, Map.of(
                KnowledgeMetadata.KNOWLEDGE_BASE, "another-project",
                KnowledgeMetadata.SOURCE, "foreign.md"
        ))));

        assertThat(searchService.search(query, 10))
                .extracting(KnowledgeSearchResult::source)
                .doesNotContain("foreign.md");
    }

    @Test
    void shouldReturnNoResultsWhenOurKnowledgeBaseIsEmpty() {
        vectorStore.delete(new FilterExpressionBuilder().eq(
                KnowledgeMetadata.KNOWLEDGE_BASE, KnowledgeMetadata.KNOWLEDGE_BASE_NAME
        ).build());

        assertThat(searchService.search("Payment status", 3)).isEmpty();
    }
}
