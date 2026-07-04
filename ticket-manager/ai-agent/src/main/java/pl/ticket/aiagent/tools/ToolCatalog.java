package pl.ticket.aiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ToolCatalog {

    private final List<ToolCallbackProvider> toolCallbackProviders;

    public ToolCatalog(List<ToolCallbackProvider> toolCallbackProviders) {
        this.toolCallbackProviders = toolCallbackProviders == null ? List.of() : List.copyOf(toolCallbackProviders);
    }

    public Optional<ToolCallback> callbackByName(String toolName) {
        return Optional.ofNullable(callbacksByName().get(toolName));
    }

    public List<ToolProviderDiagnostics> discoveredToolProviders() {
        return toolCallbackProviders.stream()
                .map(provider -> new ToolProviderDiagnostics(
                        provider.getClass().getName(),
                        Arrays.stream(provider.getToolCallbacks())
                                .map(callback -> callback.getToolDefinition().name())
                                .toList()
                ))
                .toList();
    }

    public List<String> discoveredToolNames() {
        return toolCallbackProviders.stream()
                .flatMap(provider -> Arrays.stream(provider.getToolCallbacks()))
                .map(callback -> callback.getToolDefinition().name())
                .toList();
    }

    public List<String> duplicateDiscoveredToolNames() {
        return duplicateToolNames(discoveredToolNames());
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

    private List<String> duplicateToolNames(List<String> discoveredToolNames) {
        Map<String, Integer> counts = new HashMap<>();
        return discoveredToolNames.stream()
                .filter(toolName -> counts.merge(toolName, 1, Integer::sum) == 2)
                .toList();
    }
}
