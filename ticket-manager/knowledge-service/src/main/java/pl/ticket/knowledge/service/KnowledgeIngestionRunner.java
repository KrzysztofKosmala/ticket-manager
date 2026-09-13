package pl.ticket.knowledge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ticket.knowledge.model.KnowledgeIngestionResult;

@Component
@ConditionalOnProperty(prefix = "knowledge.ingestion", name = "on-startup", havingValue = "true")
public class KnowledgeIngestionRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeIngestionRunner.class);

    private final KnowledgeIngestionService ingestionService;

    public KnowledgeIngestionRunner(KnowledgeIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        KnowledgeIngestionResult result = ingestionService.ingest();
        LOGGER.info("Knowledge ingestion completed: ingestionId={}, loadedDocuments={}, chunks={}",
                result.ingestionId(), result.loadedDocumentCount(), result.chunkCount());
    }
}
