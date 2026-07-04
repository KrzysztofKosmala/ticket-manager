package pl.ticket.aiagent.tools;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;

@Component
@Profile({"local", "test"})
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
