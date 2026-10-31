package pl.ticket.knowledge.document;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeDocumentLoaderTest {

    private final KnowledgeDocumentLoader loader = new KnowledgeDocumentLoader(new Resource[] {
            new ClassPathResource("knowledge/event-search-and-tickets.md"),
            new ClassPathResource("knowledge/cart-and-order-process.md"),
            new ClassPathResource("knowledge/order-and-payment-statuses.md"),
            new ClassPathResource("knowledge/support-boundaries.md")
    });

    @Test
    void shouldReadMarkdownDocumentsWithSourceMetadata() {
        List<Document> documents = loader.load();

        assertThat(documents).isNotEmpty();
        assertThat(documents)
                .allSatisfy(document -> {
                    assertThat(document.getText()).isNotBlank();
                    assertThat(document.getMetadata())
                            .containsEntry(KnowledgeMetadata.DOCUMENT_TYPE, "KNOWLEDGE")
                            .containsKey(KnowledgeMetadata.SOURCE);
                });
        assertThat(documents)
                .extracting(document -> document.getMetadata().get(KnowledgeMetadata.SOURCE))
                .contains(
                        "event-search-and-tickets.md",
                        "cart-and-order-process.md",
                        "order-and-payment-statuses.md",
                        "support-boundaries.md"
                );
    }
}
