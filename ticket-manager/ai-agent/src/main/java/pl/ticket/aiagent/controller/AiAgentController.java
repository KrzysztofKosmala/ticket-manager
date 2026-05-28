package pl.ticket.aiagent.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.aiagent.dto.AiAgentResponse;
import pl.ticket.aiagent.service.AiAgentService;

@RestController
@RequestMapping("/api/v1/ai-agents")
@RequiredArgsConstructor
public class AiAgentController
{

    private final AiAgentService aiAgentService;

    @PostMapping("/ask")
    public ResponseEntity<AiAgentResponse> ask(@Valid @RequestBody AskRequest request) {

        AiAgentResponse result = aiAgentService.ask(request.message());
        return ResponseEntity.ok(result);
    }

    public record AskRequest(@NotBlank String message) {}
}
