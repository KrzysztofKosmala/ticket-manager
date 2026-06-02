package pl.ticket.aiagent.tools;

import java.util.Optional;

public class ToolExecutionDeniedException extends RuntimeException {

    private final String toolName;
    private final ToolPolicyDenialReason denialReason;

    public ToolExecutionDeniedException(String toolName, ToolPolicyDenialReason denialReason) {
        super("Tool execution denied for '%s': %s".formatted(toolName, denialReason));
        this.toolName = toolName;
        this.denialReason = denialReason;
    }

    public String toolName() {
        return toolName;
    }

    public Optional<ToolPolicyDenialReason> denialReason() {
        return Optional.ofNullable(denialReason);
    }
}
