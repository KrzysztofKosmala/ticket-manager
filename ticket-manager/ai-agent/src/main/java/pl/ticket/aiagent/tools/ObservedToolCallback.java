package pl.ticket.aiagent.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.lang.Nullable;

public class ObservedToolCallback implements ToolCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservedToolCallback.class);

    private final String conversationId;
    private final String runId;
    private final ToolCallback originalCallback;
    private final ToolInvocationRecorder recorder;

    public ObservedToolCallback(String conversationId, ToolCallback originalCallback, ToolInvocationRecorder recorder) {
        this(conversationId, null, originalCallback, recorder);
    }

    public ObservedToolCallback(
            String conversationId,
            String runId,
            ToolCallback originalCallback,
            ToolInvocationRecorder recorder
    ) {
        this.conversationId = conversationId;
        this.runId = runId;
        this.originalCallback = originalCallback;
        this.recorder = recorder;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return originalCallback.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return originalCallback.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        String toolName = toolName();
        LOGGER.info("AI tool invocation started: conversationId={}, toolName={}", conversationId, toolName);
        try {
            String result = originalCallback.call(toolInput);
            recorder.recordSuccess(conversationId, runId, toolName, toolInput, result);
            LOGGER.info(
                    "AI tool invocation succeeded: conversationId={}, toolName={}, resultCharacters={}",
                    conversationId,
                    toolName,
                    lengthOf(result)
            );
            return result;
        } catch (RuntimeException exception) {
            recorder.recordFailure(conversationId, runId, toolName, toolInput, exception);
            LOGGER.warn(
                    "AI tool invocation failed: conversationId={}, toolName={}, error={}",
                    conversationId,
                    toolName,
                    exception.getMessage()
            );
            throw exception;
        }
    }

    @Override
    public String call(String toolInput, @Nullable ToolContext toolContext) {
        String toolName = toolName();
        LOGGER.info("AI tool invocation started: conversationId={}, toolName={}", conversationId, toolName);
        try {
            String result = originalCallback.call(toolInput, toolContext);
            recorder.recordSuccess(conversationId, runId, toolName, toolInput, result);
            LOGGER.info(
                    "AI tool invocation succeeded: conversationId={}, toolName={}, resultCharacters={}",
                    conversationId,
                    toolName,
                    lengthOf(result)
            );
            return result;
        } catch (RuntimeException exception) {
            recorder.recordFailure(conversationId, runId, toolName, toolInput, exception);
            LOGGER.warn(
                    "AI tool invocation failed: conversationId={}, toolName={}, error={}",
                    conversationId,
                    toolName,
                    exception.getMessage()
            );
            throw exception;
        }
    }

    private String toolName() {
        return originalCallback.getToolDefinition().name();
    }

    private int lengthOf(String value) {
        return value == null ? 0 : value.length();
    }
}
