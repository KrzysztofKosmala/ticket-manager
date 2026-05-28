package pl.ticket.aiagent.tools;

import java.util.List;

public record ToolCatalogDiagnostics(
        List<String> configuredButNotDiscoveredToolNames,
        List<String> discoveredButNotConfiguredToolNames,
        List<String> duplicateDiscoveredToolNames
) {

    public ToolCatalogDiagnostics {
        configuredButNotDiscoveredToolNames = configuredButNotDiscoveredToolNames == null
                ? List.of()
                : List.copyOf(configuredButNotDiscoveredToolNames);
        discoveredButNotConfiguredToolNames = discoveredButNotConfiguredToolNames == null
                ? List.of()
                : List.copyOf(discoveredButNotConfiguredToolNames);
        duplicateDiscoveredToolNames = duplicateDiscoveredToolNames == null
                ? List.of()
                : List.copyOf(duplicateDiscoveredToolNames);
    }
}
