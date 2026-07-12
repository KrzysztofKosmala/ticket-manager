package pl.ticket.aiagent.tools;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@ConditionalOnProperty(name = "ai-agent.persistence.mode", havingValue = "jpa")
public class JpaToolInvocationRecorder implements ToolInvocationRecorder {

    private final AiToolInvocationJpaRepository repository;

    public JpaToolInvocationRecorder(AiToolInvocationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void recordSuccess(String conversationId, String toolName, String arguments, String result) {
        recordSuccess(conversationId, null, toolName, arguments, result);
    }

    @Override
    @Transactional
    public void recordSuccess(String conversationId, String runId, String toolName, String arguments, String result) {
        repository.save(new AiToolInvocationEntity(
                conversationId,
                runId,
                toolName,
                arguments,
                result,
                ToolInvocationStatus.SUCCESS,
                null
        ));
    }

    @Override
    @Transactional
    public void recordFailure(String conversationId, String toolName, String arguments, Exception exception) {
        recordFailure(conversationId, null, toolName, arguments, exception);
    }

    @Override
    @Transactional
    public void recordFailure(String conversationId, String runId, String toolName, String arguments, Exception exception) {
        repository.save(new AiToolInvocationEntity(
                conversationId,
                runId,
                toolName,
                arguments,
                null,
                ToolInvocationStatus.FAILED,
                exception.getMessage()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToolInvocation> findByConversationId(String conversationId) {
        return repository.findAllByConversationIdOrderByIdAsc(conversationId).stream()
                .map(AiToolInvocationEntity::toToolInvocation)
                .toList();
    }
}
