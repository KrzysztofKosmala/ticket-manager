package pl.ticket.aiagent.service.run;

import pl.ticket.aiagent.model.run.AiAgentRunEntity;
import pl.ticket.aiagent.repository.run.AiAgentRunJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.ticket.aiagent.model.tools.ToolCandidate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "ai-agent.persistence.mode", havingValue = "jpa")
public class JpaAgentRunRecorder implements AgentRunRecorder {

    private final AiAgentRunJpaRepository repository;
    private final ObjectMapper objectMapper;

    public JpaAgentRunRecorder(AiAgentRunJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public String start(String conversationId, String userMessage, List<ToolCandidate> selectedTools) {
        String runId = UUID.randomUUID().toString();
        repository.save(new AiAgentRunEntity(
                runId,
                conversationId,
                userMessage,
                selectedToolNamesJson(selectedTools),
                LocalDateTime.now()
        ));
        return runId;
    }

    @Override
    @Transactional
    public void complete(String runId, String answer) {
        AiAgentRunEntity run = repository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Agent run not found"));
        run.complete(answer);
        repository.save(run);
    }

    @Override
    @Transactional
    public void fail(String runId, Exception exception) {
        AiAgentRunEntity run = repository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Agent run not found"));
        run.fail(exception);
        repository.save(run);
    }

    private String selectedToolNamesJson(List<ToolCandidate> selectedTools) {
        List<String> toolNames = selectedTools.stream()
                .map(ToolCandidate::name)
                .toList();
        try {
            return objectMapper.writeValueAsString(toolNames);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize selected tool names", exception);
        }
    }
}
