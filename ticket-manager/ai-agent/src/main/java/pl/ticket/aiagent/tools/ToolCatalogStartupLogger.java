package pl.ticket.aiagent.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ToolCatalogStartupLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolCatalogStartupLogger.class);

    private final ToolCatalog toolCatalog;

    public ToolCatalogStartupLogger(ToolCatalog toolCatalog) {
        this.toolCatalog = toolCatalog;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logToolCatalog() {
        ToolCatalogDiagnostics diagnostics = toolCatalog.diagnostics();

        LOGGER.info("AI tool catalog configured tools: {}", toolCatalog.configuredToolNames());
        if (!diagnostics.configuredButNotDiscoveredToolNames().isEmpty()) {
            LOGGER.warn(
                    "AI tool catalog configured tools missing from discovery: {}",
                    diagnostics.configuredButNotDiscoveredToolNames()
            );
        }
        if (!diagnostics.discoveredButNotConfiguredToolNames().isEmpty()) {
            LOGGER.warn(
                    "AI tool catalog discovered tools missing registry metadata: {}",
                    diagnostics.discoveredButNotConfiguredToolNames()
            );
        }
        if (!diagnostics.duplicateDiscoveredToolNames().isEmpty()) {
            LOGGER.warn(
                    "AI tool catalog duplicate discovered tool names: {}",
                    diagnostics.duplicateDiscoveredToolNames()
            );
        }
    }
}
