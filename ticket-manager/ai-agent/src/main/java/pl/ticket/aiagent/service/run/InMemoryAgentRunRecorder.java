package pl.ticket.aiagent.service.run;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.model.tools.ToolCandidate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@ConditionalOnProperty(name = "ai-agent.persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryAgentRunRecorder implements AgentRunRecorder {

    private final ConcurrentMap<String, String> statuses = new ConcurrentHashMap<>();

    @Override
    public String start(String conversationId, String userMessage, List<ToolCandidate> selectedTools) {
        String runId = UUID.randomUUID().toString();
        statuses.put(runId, "RUNNING");
        return runId;
    }

    @Override
    public void complete(String runId, String answer) {
        statuses.put(runId, "COMPLETED");
    }

    @Override
    public void fail(String runId, Exception exception) {
        statuses.put(runId, "FAILED");
    }
}
