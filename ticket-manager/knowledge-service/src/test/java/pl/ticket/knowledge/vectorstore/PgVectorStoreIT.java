package pl.ticket.knowledge.vectorstore;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "knowledge.ingestion.on-startup=false"
)
@Transactional
class PgVectorStoreIT {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldPersistDocumentWithEmbeddingAndMetadata() {
        String documentId = UUID.randomUUID().toString();
        Document document = new Document(
                documentId,
                "Bilet można zwrócić najpóźniej 24 godziny przed wydarzeniem.",
                Map.of("source", "test-return-policy.md")
        );

        try {
            vectorStore.add(List.of(document));

            Map<String, Object> storedDocument = jdbcTemplate.queryForMap("""
                    SELECT content,
                           metadata ->> 'source' AS source,
                           vector_dims(embedding) AS dimensions
                    FROM knowledge_document
                    WHERE id = ?::uuid
                    """, documentId);

            assertThat(storedDocument.get("content")).isEqualTo(document.getText());
            assertThat(storedDocument.get("source")).isEqualTo("test-return-policy.md");
            assertThat(storedDocument.get("dimensions")).isEqualTo(768);
        }
        finally {
            vectorStore.delete(List.of(documentId));
        }
    }
}
