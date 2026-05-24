package pl.ticket.aiagent.toolselection;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolcatalog.ToolCatalog;
import pl.ticket.aiagent.toolpolicy.ToolPolicy;
import pl.ticket.aiagent.toolpolicy.ToolPolicyDecision;

import java.util.List;

@Component
@Profile({"local", "test", "smoke"})
public class StaticToolCandidateSelector implements ToolCandidateSelector {

    private final ToolPolicy toolPolicy;
    private final ToolCatalog toolCatalog;

    public StaticToolCandidateSelector(ToolPolicy toolPolicy, ToolCatalog toolCatalog) {
        this.toolPolicy = toolPolicy;
        this.toolCatalog = toolCatalog;
    }

    @Override
    public List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext) {
        if (!StringUtils.hasText(userMessage)) {
            return List.of();
        }

        return toolCatalog.configuredToolNames().stream()
                .map(ToolCandidate::new)
                .filter(candidate -> isAllowed(candidate, callerContext))
                .toList();
    }

    private boolean isAllowed(ToolCandidate candidate, CallerContext callerContext) {
        ToolPolicyDecision decision = toolPolicy.evaluate(candidate, callerContext);
        return decision.allowed();
    }
}
