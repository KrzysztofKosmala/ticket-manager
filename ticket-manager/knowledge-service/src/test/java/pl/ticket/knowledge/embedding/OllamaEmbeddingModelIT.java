package pl.ticket.knowledge.embedding;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "knowledge.ingestion.on-startup=false"
)
class OllamaEmbeddingModelIT {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Test
    void shouldCreateEmbeddingWithExpectedDimensions() {
        float[] embedding = embeddingModel.embed("Czy mogę zwrócić kupiony bilet?");

        assertThat(embedding).hasSize(768);
    }
}
