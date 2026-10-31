package pl.ticket.knowledge.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.ticket.knowledge.model.KnowledgeIngestionResult;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "knowledge.ingestion.on-startup=false"
)
@Transactional
class KnowledgeIngestionIT {

    @Autowired
    private KnowledgeIngestionService ingestionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldIngestCurrentKnowledgeBase() {
        ingestionService.ingest();

        Integer sourceCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(DISTINCT metadata ->> 'source')
                FROM knowledge_document
                WHERE metadata ->> 'knowledgeBase' = 'ticket-manager'
                """, Integer.class);
        Integer ingestionCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(DISTINCT metadata ->> 'ingestionId')
                FROM knowledge_document
                WHERE metadata ->> 'knowledgeBase' = 'ticket-manager'
                """, Integer.class);
        Boolean allEmbeddingsHaveExpectedDimensions = jdbcTemplate.queryForObject("""
                SELECT COALESCE(BOOL_AND(vector_dims(embedding) = 768), false)
                FROM knowledge_document
                WHERE metadata ->> 'knowledgeBase' = 'ticket-manager'
                """, Boolean.class);

        assertThat(sourceCount).isEqualTo(4);
        assertThat(ingestionCount).isEqualTo(1);
        assertThat(allEmbeddingsHaveExpectedDimensions).isTrue();
    }

    @Test
    void shouldReplacePreviousSnapshotWithoutDuplicatingChunks() {
        KnowledgeIngestionResult first = ingestionService.ingest();
        KnowledgeIngestionResult second = ingestionService.ingest();

        Integer currentCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM knowledge_document
                WHERE metadata ->> 'ingestionId' = ?
                """, Integer.class, second.ingestionId());
        Integer previousCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM knowledge_document
                WHERE metadata ->> 'ingestionId' = ?
                """, Integer.class, first.ingestionId());

        assertThat(currentCount).isEqualTo(second.chunkCount());
        assertThat(previousCount).isZero();
    }
}
