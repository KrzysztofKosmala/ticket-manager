package pl.ticket.aiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.security.CallerContext;

import java.util.ArrayList;
import java.util.List;

@Component
public class SelectedToolCallbackResolver {

    private final ToolCatalog toolCatalog;
    private final ToolPolicy toolPolicy;

    public SelectedToolCallbackResolver(ToolCatalog toolCatalog, ToolPolicy toolPolicy) {
        this.toolCatalog = toolCatalog;
        this.toolPolicy = toolPolicy;
    }

    public ToolCallbackResolution resolve(List<ToolCandidate> candidates, CallerContext callerContext) {
        if (candidates == null || candidates.isEmpty()) {
            return ToolCallbackResolution.empty();
        }

        List<ToolCallback> resolvedCallbacks = new ArrayList<>();
        List<ToolCandidate> missingCandidates = new ArrayList<>();

        for (ToolCandidate candidate : candidates) {
            toolCatalog.callbackByName(candidate.name())
                    .ifPresentOrElse(
                            callback -> resolvedCallbacks.add(protectedCallback(callback, candidate, callerContext)),
                            () -> missingCandidates.add(candidate)
                    );
        }

        return new ToolCallbackResolution(resolvedCallbacks, missingCandidates);
    }

    private ToolCallback protectedCallback(ToolCallback callback, ToolCandidate candidate, CallerContext callerContext) {
        return new PolicyEnforcingToolCallback(callback, candidate, callerContext, toolPolicy);
    }
}
