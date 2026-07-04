package pl.ticket.aiagent.tools;

import java.util.List;

public record ToolProviderDiagnostics(
        String providerClassName,
        List<String> toolNames
) {

    public ToolProviderDiagnostics {
        toolNames = toolNames == null ? List.of() : List.copyOf(toolNames);
    }

    @Override
    public String toString() {
        return providerClassName + "=" + toolNames;
    }
}
