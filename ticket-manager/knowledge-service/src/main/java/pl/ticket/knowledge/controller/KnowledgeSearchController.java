package pl.ticket.knowledge.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.knowledge.service.KnowledgeSearchService;
import pl.ticket.dto.KnowledgeSearchRequest;
import pl.ticket.dto.KnowledgeSearchResult;

import java.util.List;

@RestController
@RequestMapping("/internal/knowledge")
public class KnowledgeSearchController {

    private final KnowledgeSearchService searchService;

    public KnowledgeSearchController(KnowledgeSearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    public List<KnowledgeSearchResult> search(@Valid @RequestBody KnowledgeSearchRequest request) {
        return searchService.search(request.query(), request.topK());
    }
}
