package pl.ticket.aiagent.tools;

import java.util.List;

public interface ToolInvocationRecorder {

    void recordSuccess(String conversationId, String toolName, String arguments, String result);

    void recordFailure(String conversationId, String toolName, String arguments, Exception exception);

    List<ToolInvocation> findByConversationId(String conversationId);
}
