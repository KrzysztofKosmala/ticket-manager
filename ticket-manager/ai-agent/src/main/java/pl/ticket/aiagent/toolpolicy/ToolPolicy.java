package pl.ticket.aiagent.toolpolicy;

import org.springframework.stereotype.Component;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolselection.ToolCandidate;

@Component
public class ToolPolicy {

    private final ToolPolicyProperties properties;

    public ToolPolicy(ToolPolicyProperties properties) {
        this.properties = properties;
    }

    public ToolPolicyDecision evaluate(ToolCandidate candidate, CallerContext callerContext) {
        if (!properties.getAllowList().contains(candidate.name())) {
            return ToolPolicyDecision.deny(ToolPolicyDenialReason.NOT_ALLOW_LISTED);
        }

        ToolPolicyProperties.ToolMetadata metadata = properties.metadataFor(candidate.name());
        if (!metadata.isEnabled()) {
            return ToolPolicyDecision.deny(ToolPolicyDenialReason.DISABLED);
        }

        if (metadata.getAccessMode() == ToolAccessMode.WRITE) {
            return ToolPolicyDecision.deny(ToolPolicyDenialReason.WRITE_SIDE_UNSUPPORTED);
        }

        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        if (properties.isEnforceScopes() && !effectiveCallerContext.hasAllScopes(metadata.getRequiredScopes())) {
            return ToolPolicyDecision.deny(ToolPolicyDenialReason.MISSING_SCOPE);
        }

        return ToolPolicyDecision.allow();
    }
}
