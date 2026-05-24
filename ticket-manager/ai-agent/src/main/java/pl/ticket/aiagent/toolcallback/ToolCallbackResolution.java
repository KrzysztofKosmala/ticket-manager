package pl.ticket.aiagent.toolcallback;

import org.springframework.ai.tool.ToolCallback;
import pl.ticket.aiagent.toolselection.ToolCandidate;

import java.util.List;

public record ToolCallbackResolution(
        List<ToolCallback> callbacks,
        List<ToolCandidate> missingCandidates
) {

    public ToolCallbackResolution {
        callbacks = callbacks == null ? List.of() : List.copyOf(callbacks);
        missingCandidates = missingCandidates == null ? List.of() : List.copyOf(missingCandidates);
    }

    public static ToolCallbackResolution of(List<ToolCallback> callbacks) {
        return new ToolCallbackResolution(callbacks, List.of());
    }

    public static ToolCallbackResolution empty() {
        return new ToolCallbackResolution(List.of(), List.of());
    }

    public boolean hasCallbacks() {
        return !callbacks.isEmpty();
    }
}
