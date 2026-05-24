package pl.ticket.aiagent.toolcatalog;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.toolpolicy.ToolPolicyProperties;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ToolCatalog {

    private final ToolPolicyProperties toolPolicyProperties;
    private final List<ToolCallbackProvider> toolCallbackProviders;

    public ToolCatalog(ToolPolicyProperties toolPolicyProperties, List<ToolCallbackProvider> toolCallbackProviders) {
        this.toolPolicyProperties = toolPolicyProperties;
        this.toolCallbackProviders = toolCallbackProviders == null ? List.of() : List.copyOf(toolCallbackProviders);
    }

    public List<String> configuredToolNames() {
        return toolPolicyProperties.getAllowList();
    }

    public Optional<ToolCallback> callbackByName(String toolName) {
        return Optional.ofNullable(callbacksByName().get(toolName));
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
}
