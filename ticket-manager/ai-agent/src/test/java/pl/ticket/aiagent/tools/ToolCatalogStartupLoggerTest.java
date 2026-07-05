package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class ToolCatalogStartupLoggerTest {

    @Test
    void shouldLogConfiguredToolsAndDiagnostics(CapturedOutput output) {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(registryWith("tm_my_orders_search", "tm_knowledge_search"));
        ToolCatalog catalog = mock(ToolCatalog.class);
        when(catalog.discoveredToolProviders()).thenReturn(List.of(
                new ToolProviderDiagnostics("mcpProvider", List.of("tm_my_orders_search", "tm_internal_unconfigured"))
        ));
        when(catalog.discoveredToolNames()).thenReturn(List.of(
                "tm_my_orders_search",
                "tm_internal_unconfigured",
                "tm_my_orders_search"
        ));
        when(catalog.duplicateDiscoveredToolNames()).thenReturn(List.of("tm_my_orders_search"));
        ToolCatalogStartupLogger startupLogger = new ToolCatalogStartupLogger(properties, catalog);

        startupLogger.logToolCatalog();

        assertThat(output)
                .contains("AI tool catalog configured tools: [tm_my_orders_search, tm_knowledge_search]")
                .contains("AI tool catalog discovered providers: [mcpProvider=[tm_my_orders_search, tm_internal_unconfigured]]")
                .contains("AI tool catalog configured tools missing from discovery: [tm_knowledge_search]")
                .contains("AI tool catalog discovered tools missing registry metadata: [tm_internal_unconfigured]")
                .contains("AI tool catalog duplicate discovered tool names: [tm_my_orders_search]");
    }

    @Test
    void shouldSkipEmptyDiagnosticWarnings(CapturedOutput output) {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(registryWith("tm_my_orders_search"));
        ToolCatalog catalog = mock(ToolCatalog.class);
        when(catalog.discoveredToolProviders()).thenReturn(List.of());
        when(catalog.discoveredToolNames()).thenReturn(List.of("tm_my_orders_search"));
        when(catalog.duplicateDiscoveredToolNames()).thenReturn(List.of());
        ToolCatalogStartupLogger startupLogger = new ToolCatalogStartupLogger(properties, catalog);

        startupLogger.logToolCatalog();

        assertThat(output)
                .contains("AI tool catalog configured tools: [tm_my_orders_search]")
                .contains("AI tool catalog discovered providers: []")
                .doesNotContain("missing from discovery")
                .doesNotContain("missing registry metadata")
                .doesNotContain("duplicate discovered tool names");
    }

    private java.util.Map<String, ToolPolicyProperties.ToolMetadata> registryWith(String... toolNames) {
        java.util.Map<String, ToolPolicyProperties.ToolMetadata> registry = new java.util.LinkedHashMap<>();
        for (String toolName : toolNames) {
            ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
            metadata.setSource(ToolSourceType.INTERNAL_MCP);
            metadata.setAccessMode(ToolAccessMode.READ);
            registry.put(toolName, metadata);
        }
        return registry;
    }
}
