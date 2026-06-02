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

    public ToolCallbackResolution resolve(List<ToolCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return ToolCallbackResolution.empty();
        }

        List<ToolCallback> resolvedCallbacks = new ArrayList<>();
        List<ToolCandidate> missingCandidates = new ArrayList<>();

        for (ToolCandidate candidate : candidates) {
            toolCatalog.callbackByName(candidate.name())
                    .ifPresentOrElse(
                            resolvedCallbacks::add,
                            () -> missingCandidates.add(candidate)
                    );
        }

        return new ToolCallbackResolution(resolvedCallbacks, missingCandidates);
    }
}
