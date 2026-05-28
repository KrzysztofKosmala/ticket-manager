package pl.ticket.aiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ToolCatalog {

    private final ToolPolicyProperties toolPolicyProperties;
    private final List<ToolCallbackProvider> toolCallbackProviders;

    public ToolCatalog(ToolPolicyProperties toolPolicyProperties, List<ToolCallbackProvider> toolCallbackProviders) {
        this.toolPolicyProperties = toolPolicyProperties;
        this.toolCallbackProviders = toolCallbackProviders == null ? List.of() : List.copyOf(toolCallbackProviders);
    }

    public List<String> configuredToolNames() {
        return toolPolicyProperties.enabledToolNames();
    }

    public Optional<ToolCallback> callbackByName(String toolName) {
        return Optional.ofNullable(callbacksByName().get(toolName));
    }

    public ToolCatalogDiagnostics diagnostics() {
        Set<String> configuredToolNames = new LinkedHashSet<>(toolPolicyProperties.getRegistry().keySet());
        Set<String> enabledToolNames = new LinkedHashSet<>(configuredToolNames());
        List<String> discoveredToolNames = discoveredToolNames();
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
                duplicateToolNames(discoveredToolNames)
        );
    }

    private Map<String, ToolCallback> callbacksByName() {
        Map<String, ToolCallback> callbacksByName = new LinkedHashMap<>();
        for (ToolCallbackProvider provider : toolCallbackProviders) {
            Arrays.stream(provider.getToolCallbacks())
                    .forEach(callback -> callbacksByName.putIfAbsent(
                            callback.getToolDefinition().name(),
                            callback
                    ));
        }
        return callbacksByName;
    }

    private List<String> discoveredToolNames() {
        return toolCallbackProviders.stream()
                .flatMap(provider -> Arrays.stream(provider.getToolCallbacks()))
                .map(callback -> callback.getToolDefinition().name())
                .toList();
    }

    private List<String> duplicateToolNames(List<String> discoveredToolNames) {
        Map<String, Integer> counts = new HashMap<>();
        return discoveredToolNames.stream()
                .filter(toolName -> counts.merge(toolName, 1, Integer::sum) == 2)
                .toList();
    }
}
