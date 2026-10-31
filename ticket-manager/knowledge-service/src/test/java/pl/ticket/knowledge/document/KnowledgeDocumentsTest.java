package pl.ticket.knowledge.document;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeDocumentsTest {

    private static final List<String> DOCUMENTS = List.of(
            "knowledge/event-search-and-tickets.md",
            "knowledge/cart-and-order-process.md",
            "knowledge/order-and-payment-statuses.md",
            "knowledge/support-boundaries.md"
    );

    @Test
    void shouldProvideNonEmptyMarkdownDocuments() throws IOException {
        for (String documentPath : DOCUMENTS) {
            ClassPathResource resource = new ClassPathResource(documentPath);

            assertThat(resource.exists()).as(documentPath).isTrue();
            assertThat(resource.getContentAsString(StandardCharsets.UTF_8))
                    .as(documentPath)
                    .startsWith("# ")
                    .contains("\n## ");
        }
    }
}
