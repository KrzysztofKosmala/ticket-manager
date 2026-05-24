package pl.ticket.aiagent.toolselection;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolpolicy.ToolPolicy;
import pl.ticket.aiagent.toolpolicy.ToolPolicyDecision;
import pl.ticket.aiagent.toolpolicy.ToolPolicyProperties;

import java.util.List;

@Component
@Profile({"local", "test", "smoke"})
public class StaticToolCandidateSelector implements ToolCandidateSelector {

    private final ToolPolicy toolPolicy;
    private final ToolPolicyProperties toolPolicyProperties;

    public StaticToolCandidateSelector(ToolPolicy toolPolicy, ToolPolicyProperties toolPolicyProperties) {
        this.toolPolicy = toolPolicy;
        this.toolPolicyProperties = toolPolicyProperties;
    }

    @Override
    public List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext) {
        if (!StringUtils.hasText(userMessage)) {
            return List.of();
        }

        return toolPolicyProperties.getAllowList().stream()
                .map(ToolCandidate::new)
                .filter(candidate -> isAllowed(candidate, callerContext))
                .toList();
    }

    private boolean isAllowed(ToolCandidate candidate, CallerContext callerContext) {
        ToolPolicyDecision decision = toolPolicy.evaluate(candidate, callerContext);
        return decision.allowed();
    }
}
