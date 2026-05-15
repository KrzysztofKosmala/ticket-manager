package pl.ticket.aiagent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.ticket.aiagent.tool.CallerContext;
import pl.ticket.aiagent.planner.Plan;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai-agents")
@RequiredArgsConstructor
public class AiAgentController
{

    private final AiAgentService aiAgentService;
    @GetMapping("/check")
    public String registerCustomer()
    {
        return aiAgentService.testPrompt("Cześć, jak się masz?");

    }

    @PostMapping("/plan")
    public ResponseEntity<Plan> plan(@Valid @RequestBody PlanRequest request) {
        Plan plan = aiAgentService.plan(request.message());
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/ask")
    public ResponseEntity<PlanExecutionResult> execute(@Valid @RequestBody PlanRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {

        PlanExecutionResult result = aiAgentService.executePlan(request.message(), CallerContext.from(jwt));
        return ResponseEntity.ok(result);
    }

    public record PlanRequest(@NotBlank String message) {}
}
