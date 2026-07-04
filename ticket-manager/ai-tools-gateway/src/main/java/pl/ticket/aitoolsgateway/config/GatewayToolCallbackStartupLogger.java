package pl.ticket.aitoolsgateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GatewayToolCallbackStartupLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayToolCallbackStartupLogger.class);

    private final List<ToolCallbackProvider> toolCallbackProviders;

    public GatewayToolCallbackStartupLogger(List<ToolCallbackProvider> toolCallbackProviders) {
        this.toolCallbackProviders = toolCallbackProviders == null ? List.of() : List.copyOf(toolCallbackProviders);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logToolCallbackProviders() {
        LOGGER.info("AI tools gateway callback providers: {}", toolCallbackProviders.stream()
                .map(provider -> provider.getClass().getName() + "=" + toolNames(provider))
                .toList());
    }

    private List<String> toolNames(ToolCallbackProvider provider) {
        return Arrays.stream(provider.getToolCallbacks())
                .map(callback -> callback.getToolDefinition().name())
                .toList();
    }
}
