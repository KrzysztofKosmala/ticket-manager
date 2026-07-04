package pl.ticket.aiagent.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class ToolCatalogStartupLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolCatalogStartupLogger.class);

    private final ToolPolicyProperties toolPolicyProperties;
    private final ToolCatalog toolCatalog;

    public ToolCatalogStartupLogger(ToolPolicyProperties toolPolicyProperties, ToolCatalog toolCatalog) {
        this.toolPolicyProperties = toolPolicyProperties;
        this.toolCatalog = toolCatalog;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logToolCatalog() {
        ToolCatalogDiagnostics diagnostics = diagnostics();

        LOGGER.info("AI tool catalog configured tools: {}", toolPolicyProperties.enabledToolNames());
        LOGGER.info("AI tool catalog discovered providers: {}", toolCatalog.discoveredToolProviders());
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

    private ToolCatalogDiagnostics diagnostics() {
        Set<String> configuredToolNames = new LinkedHashSet<>(toolPolicyProperties.getRegistry().keySet());
        Set<String> enabledToolNames = new LinkedHashSet<>(toolPolicyProperties.enabledToolNames());
        List<String> discoveredToolNames = toolCatalog.discoveredToolNames();
        Set<String> uniqueDiscoveredToolNames = new LinkedHashSet<>(discoveredToolNames);

        List<String> configuredButNotDiscovered = enabledToolNames.stream()
                .filter(toolName -> !uniqueDiscoveredToolNames.contains(toolName))
                .toList();

        List<String> discoveredButNotConfigured = uniqueDiscoveredToolNames.stream()
                .filter(toolName -> !configuredToolNames.contains(toolName))
                .toList();

        return new ToolCatalogDiagnostics(
                configuredButNotDiscovered,
                discoveredButNotConfigured,
                toolCatalog.duplicateDiscoveredToolNames()
        );
    }
}
