package pl.ticket.aiagent.toolcallback;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.toolselection.ToolCandidate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SelectedToolCallbackResolver {

    private final List<ToolCallbackProvider> toolCallbackProviders;

    public SelectedToolCallbackResolver(List<ToolCallbackProvider> toolCallbackProviders) {
        this.toolCallbackProviders = toolCallbackProviders == null ? List.of() : List.copyOf(toolCallbackProviders);
    }

    public ToolCallbackResolution resolve(List<ToolCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return ToolCallbackResolution.empty();
        }

        Map<String, ToolCallback> callbacksByName = callbacksByName();
        List<ToolCallback> resolvedCallbacks = new ArrayList<>();
        List<ToolCandidate> missingCandidates = new ArrayList<>();

        for (ToolCandidate candidate : candidates) {
            ToolCallback callback = callbacksByName.get(candidate.name());
            if (callback == null) {
                missingCandidates.add(candidate);
                continue;
            }
            resolvedCallbacks.add(callback);
        }

        return new ToolCallbackResolution(resolvedCallbacks, missingCandidates);
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
