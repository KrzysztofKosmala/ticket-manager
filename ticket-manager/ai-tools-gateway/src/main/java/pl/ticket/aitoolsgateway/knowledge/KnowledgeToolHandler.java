package pl.ticket.aitoolsgateway.knowledge;

import pl.ticket.dto.KnowledgeSearchResult;

import java.util.List;

public interface KnowledgeToolHandler {

    List<KnowledgeSearchResult> searchKnowledge(String query);
}
