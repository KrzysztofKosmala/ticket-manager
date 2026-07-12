package pl.ticket.aiagent.run;

import pl.ticket.aiagent.tools.ToolCandidate;

import java.util.List;

public interface AgentRunRecorder {

    String start(String conversationId, String userMessage, List<ToolCandidate> selectedTools);

    void complete(String runId, String answer);

    void fail(String runId, Exception exception);
}
