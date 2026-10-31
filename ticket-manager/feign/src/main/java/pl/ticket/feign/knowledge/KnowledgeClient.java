package pl.ticket.feign.knowledge;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.ticket.dto.KnowledgeSearchRequest;
import pl.ticket.dto.KnowledgeSearchResult;

import java.util.List;

@FeignClient(name = "knowledge-service", url = "${clients.knowledge-service.url:http://localhost:8111}")
public interface KnowledgeClient {

    @PostMapping("/internal/knowledge/search")
    List<KnowledgeSearchResult> search(@RequestBody KnowledgeSearchRequest request);
}
