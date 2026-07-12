package pl.ticket.aiagent.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.model.tools.ToolCandidate;

import java.util.List;

@Component
public class AiAgentFlowLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiAgentFlowLogger.class);

    public void requestStarted(String conversationId, List<ToolCandidate> selectedTools) {
        LOGGER.info(
                "AI agent request started: conversationId={}, selectedTools={}",
                conversationId,
                selectedToolNames(selectedTools)
        );
    }

    public void toolCallbacksResolved(String conversationId, List<ToolCallback> callbacks) {
        LOGGER.info(
                "AI agent resolved tool callbacks: conversationId={}, callbacks={}",
                conversationId,
                callbackNames(callbacks)
        );
    }

    public void requestCompleted(String conversationId) {
        LOGGER.info("AI agent request completed: conversationId={}", conversationId);
    }

    private List<String> selectedToolNames(List<ToolCandidate> selectedTools) {
        return selectedTools.stream()
                .map(ToolCandidate::name)
                .toList();
    }

    private List<String> callbackNames(List<ToolCallback> callbacks) {
        return callbacks.stream()
                .map(callback -> callback.getToolDefinition().name())
                .toList();
    }
}
