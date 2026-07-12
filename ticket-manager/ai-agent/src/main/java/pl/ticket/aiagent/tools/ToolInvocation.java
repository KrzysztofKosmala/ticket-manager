package pl.ticket.aiagent.tools;

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
