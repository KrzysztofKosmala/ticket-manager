package pl.ticket.aiagent.tools;

import java.util.List;

public interface ToolInvocationRecorder {

    void recordSuccess(String conversationId, String toolName, String arguments, String result);

    default void recordSuccess(String conversationId, String runId, String toolName, String arguments, String result) {
        recordSuccess(conversationId, toolName, arguments, result);
    }

    void recordFailure(String conversationId, String toolName, String arguments, Exception exception);

    default void recordFailure(String conversationId, String runId, String toolName, String arguments, Exception exception) {
        recordFailure(conversationId, toolName, arguments, exception);
    }

    List<ToolInvocation> findByConversationId(String conversationId);
}
