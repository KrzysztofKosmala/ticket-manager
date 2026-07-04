package pl.ticket.aiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SelectedToolCallbackResolver {

    private final ToolCatalog toolCatalog;

    public SelectedToolCallbackResolver(ToolCatalog toolCatalog) {
        this.toolCatalog = toolCatalog;
    }

    public List<ToolCallback> resolve(List<ToolCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        List<ToolCallback> callbacks = new ArrayList<>();

        for (ToolCandidate candidate : candidates) {
            ToolCallback callback = toolCatalog.callbackByName(candidate.name())
                    .orElseThrow(() -> new IllegalStateException(
                            "Selected tool callback was not discovered: " + candidate.name()
                    ));
            callbacks.add(callback);
        }

        return callbacks;
    }
}
