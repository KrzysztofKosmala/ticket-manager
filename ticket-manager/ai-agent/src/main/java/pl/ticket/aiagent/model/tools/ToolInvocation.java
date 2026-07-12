package pl.ticket.aiagent.model.tools;

public record ToolInvocation(
        String conversationId,
        String runId,
        String toolName,
        String arguments,
        ToolInvocationStatus status,
        String result,
        String errorMessage
) {
}
