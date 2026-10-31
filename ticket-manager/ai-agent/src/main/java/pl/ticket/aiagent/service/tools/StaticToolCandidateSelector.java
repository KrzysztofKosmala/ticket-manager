package pl.ticket.aiagent.service.tools;

import pl.ticket.aiagent.model.tools.ToolCandidate;
import pl.ticket.aiagent.model.tools.ToolPolicyDecision;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;

@Component
@ConditionalOnProperty(name = "ai-agent.tools.selection-mode", havingValue = "static")
public class StaticToolCandidateSelector implements ToolCandidateSelector {

    private final ToolPolicy toolPolicy;

    public StaticToolCandidateSelector(ToolPolicy toolPolicy) {
        this.toolPolicy = toolPolicy;
    }

    @Override
    public List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext) {
        if (!StringUtils.hasText(userMessage)) {
            return List.of();
        }

        return toolPolicy.enabledToolNames().stream()
                .map(ToolCandidate::new)
                .filter(candidate -> isAllowed(candidate, callerContext))
                .toList();
    }

    private boolean isAllowed(ToolCandidate candidate, CallerContext callerContext) {
        ToolPolicyDecision decision = toolPolicy.evaluate(candidate, callerContext);
        return decision.allowed();
    }
}
