package pl.ticket.aiagent.tools;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ConditionalOnProperty(name = "ai-agent.persistence.mode", havingValue = "memory", matchIfMissing = true)
public class InMemoryToolInvocationRecorder implements ToolInvocationRecorder {

    private final ConcurrentMap<String, CopyOnWriteArrayList<ToolInvocation>> invocations = new ConcurrentHashMap<>();

    @Override
    public void recordSuccess(String conversationId, String toolName, String arguments, String result) {
        append(new ToolInvocation(
                conversationId,
                null,
                toolName,
                arguments,
                ToolInvocationStatus.SUCCESS,
                result,
                null
        ));
    }

    @Override
    public void recordFailure(String conversationId, String toolName, String arguments, Exception exception) {
        append(new ToolInvocation(
                conversationId,
                null,
                toolName,
                arguments,
                ToolInvocationStatus.FAILED,
                null,
                exception.getMessage()
        ));
    }

    @Override
    public List<ToolInvocation> findByConversationId(String conversationId) {
        return List.copyOf(invocations.getOrDefault(conversationId, new CopyOnWriteArrayList<>()));
    }

    private void append(ToolInvocation invocation) {
        invocations.computeIfAbsent(invocation.conversationId(), id -> new CopyOnWriteArrayList<>())
                .add(invocation);
    }
}
