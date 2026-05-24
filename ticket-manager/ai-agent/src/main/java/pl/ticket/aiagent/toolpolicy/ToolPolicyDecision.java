package pl.ticket.aiagent.toolpolicy;

import java.util.Optional;

public record ToolPolicyDecision(
        boolean allowed,
        Optional<ToolPolicyDenialReason> denialReason
) {

    public ToolPolicyDecision {
        denialReason = denialReason == null ? Optional.empty() : denialReason;
    }

    public static ToolPolicyDecision allow() {
        return new ToolPolicyDecision(true, Optional.empty());
    }

    public static ToolPolicyDecision deny(ToolPolicyDenialReason reason) {
        return new ToolPolicyDecision(false, Optional.of(reason));
    }
}
