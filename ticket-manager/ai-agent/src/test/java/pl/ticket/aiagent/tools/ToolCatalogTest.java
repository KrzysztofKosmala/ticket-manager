package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToolCatalogTest {

    @Test
    void shouldExposeConfiguredToolNamesInOrder() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(registryWith("tm.orders.search", "tm.knowledge.search"));
        ToolCatalog catalog = new ToolCatalog(properties, List.of());

        assertThat(catalog.configuredToolNames())
                .containsExactly("tm.orders.search", "tm.knowledge.search");
    }

    @Test
    void shouldExposeOnlyEnabledConfiguredToolNames() {
        ToolPolicyProperties.ToolMetadata disabledMetadata = metadata();
        disabledMetadata.setEnabled(false);
        Map<String, ToolPolicyProperties.ToolMetadata> registry = new LinkedHashMap<>();
        registry.put("tm.orders.search", metadata());
        registry.put("tm.knowledge.search", disabledMetadata);
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(registry);
        ToolCatalog catalog = new ToolCatalog(properties, List.of());

        assertThat(catalog.configuredToolNames())
                .containsExactly("tm.orders.search");
    }

    @Test
    void shouldResolveDiscoveredCallbackByName() {
        ToolCallback orderSearchCallback = callbackNamed("tm.orders.search");
        ToolCallbackProvider provider = providerReturning(orderSearchCallback);
        ToolCatalog catalog = new ToolCatalog(new ToolPolicyProperties(), List.of(provider));

        assertThat(catalog.callbackByName("tm.orders.search"))
                .contains(orderSearchCallback);
    }

    @Test
    void shouldReturnEmptyOptionalWhenCallbackWasNotDiscovered() {
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm.orders.search"));
        ToolCatalog catalog = new ToolCatalog(new ToolPolicyProperties(), List.of(provider));

        assertThat(catalog.callbackByName("tm.knowledge.search"))
                .isEmpty();
    }

    @Test
    void shouldReportConfiguredToolsMissingFromDiscovery() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(registryWith("tm.orders.search", "tm.knowledge.search"));
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm.orders.search"));
        ToolCatalog catalog = new ToolCatalog(properties, List.of(provider));

        ToolCatalogDiagnostics diagnostics = catalog.diagnostics();

        assertThat(diagnostics.configuredButNotDiscoveredToolNames())
                .containsExactly("tm.knowledge.search");
    }

    @Test
    void shouldNotReportDisabledConfiguredToolsMissingFromDiscovery() {
        ToolPolicyProperties.ToolMetadata disabledMetadata = metadata();
        disabledMetadata.setEnabled(false);
        Map<String, ToolPolicyProperties.ToolMetadata> registry = new LinkedHashMap<>();
        registry.put("tm.orders.search", metadata());
        registry.put("tm.knowledge.search", disabledMetadata);
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(registry);
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm.orders.search"));
        ToolCatalog catalog = new ToolCatalog(properties, List.of(provider));

        ToolCatalogDiagnostics diagnostics = catalog.diagnostics();

        assertThat(diagnostics.configuredButNotDiscoveredToolNames())
                .isEmpty();
    }

    @Test
    void shouldReportDiscoveredToolsMissingFromConfiguration() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(Map.of("tm.orders.search", metadata()));
        ToolCallbackProvider provider = providerReturning(
                callbackNamed("tm.orders.search"),
                callbackNamed("tm.internal.unconfigured")
        );
        ToolCatalog catalog = new ToolCatalog(properties, List.of(provider));

        ToolCatalogDiagnostics diagnostics = catalog.diagnostics();

        assertThat(diagnostics.discoveredButNotConfiguredToolNames())
                .containsExactly("tm.internal.unconfigured");
    }

    @Test
    void shouldReportDuplicateDiscoveredToolNames() {
        ToolCallbackProvider firstProvider = providerReturning(callbackNamed("tm.orders.search"));
        ToolCallbackProvider secondProvider = providerReturning(
                callbackNamed("tm.orders.search"),
                callbackNamed("tm.orders.search"),
                callbackNamed("tm.knowledge.search")
        );
        ToolCatalog catalog = new ToolCatalog(new ToolPolicyProperties(), List.of(firstProvider, secondProvider));

        ToolCatalogDiagnostics diagnostics = catalog.diagnostics();

        assertThat(diagnostics.duplicateDiscoveredToolNames())
                .containsExactly("tm.orders.search");
    }

    private ToolCallbackProvider providerReturning(ToolCallback... callbacks) {
        ToolCallbackProvider provider = mock(ToolCallbackProvider.class);
        when(provider.getToolCallbacks()).thenReturn(callbacks);
        return provider;
    }

    private Map<String, ToolPolicyProperties.ToolMetadata> registryWith(String... toolNames) {
        Map<String, ToolPolicyProperties.ToolMetadata> registry = new LinkedHashMap<>();
        for (String toolName : toolNames) {
            registry.put(toolName, metadata());
        }
        return registry;
    }

    private ToolPolicyProperties.ToolMetadata metadata() {
        ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
        metadata.setSource(pl.ticket.aiagent.tools.ToolSourceType.INTERNAL_MCP);
        metadata.setAccessMode(pl.ticket.aiagent.tools.ToolAccessMode.READ);
        return metadata;
    }

    private ToolCallback callbackNamed(String name) {
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        when(toolDefinition.name()).thenReturn(name);

        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition()).thenReturn(toolDefinition);
        return callback;
    }
}
