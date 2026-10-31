package pl.ticket.knowledge.vectorstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "knowledge.ingestion.on-startup=false"
)
class HnswIndexIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateHnswIndexForCosineDistance() {
        String indexDefinition = jdbcTemplate.queryForObject("""
                SELECT indexdef
                FROM pg_indexes
                WHERE schemaname = 'public'
                  AND tablename = 'knowledge_document'
                  AND indexname = 'knowledge_document_embedding_hnsw_idx'
                """, String.class);

        assertThat(indexDefinition)
                .contains("USING hnsw")
                .contains("vector_cosine_ops");
    }
}
