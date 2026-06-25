package pl.ticket.aiagent.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.lang.Nullable;

public class ObservedToolCallback implements ToolCallback {

    private final String conversationId;
    private final ToolCallback delegate;
    private final ToolInvocationRecorder recorder;

    public ObservedToolCallback(String conversationId, ToolCallback delegate, ToolInvocationRecorder recorder) {
        this.conversationId = conversationId;
        this.delegate = delegate;
        this.recorder = recorder;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return delegate.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        try {
            String result = delegate.call(toolInput);
            recorder.recordSuccess(conversationId, toolName(), toolInput, result);
            return result;
        } catch (RuntimeException exception) {
            recorder.recordFailure(conversationId, toolName(), toolInput, exception);
            throw exception;
        }
    }

    @Override
    public String call(String toolInput, @Nullable ToolContext toolContext) {
        try {
            String result = delegate.call(toolInput, toolContext);
            recorder.recordSuccess(conversationId, toolName(), toolInput, result);
            return result;
        } catch (RuntimeException exception) {
            recorder.recordFailure(conversationId, toolName(), toolInput, exception);
            throw exception;
        }
    }

    private String toolName() {
        return delegate.getToolDefinition().name();
    }
}
