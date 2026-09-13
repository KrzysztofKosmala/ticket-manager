package pl.ticket.aitoolsgateway.knowledge;

import org.springframework.stereotype.Component;
import pl.ticket.dto.KnowledgeSearchRequest;
import pl.ticket.dto.KnowledgeSearchResult;
import pl.ticket.feign.knowledge.KnowledgeClient;

import java.util.List;

@Component
public class KnowledgeServiceToolHandler implements KnowledgeToolHandler {

    private static final int DEFAULT_TOP_K = 3;

    private final KnowledgeClient knowledgeClient;

    public KnowledgeServiceToolHandler(KnowledgeClient knowledgeClient) {
        this.knowledgeClient = knowledgeClient;
    }

    @Override
    public List<KnowledgeSearchResult> searchKnowledge(String query) {
        return knowledgeClient.search(new KnowledgeSearchRequest(query, DEFAULT_TOP_K));
    }
}
