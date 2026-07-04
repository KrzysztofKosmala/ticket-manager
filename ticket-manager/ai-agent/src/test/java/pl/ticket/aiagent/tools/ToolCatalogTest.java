package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToolCatalogTest {

    @Test
    void shouldResolveDiscoveredCallbackByName() {
        ToolCallback orderSearchCallback = callbackNamed("tm_orders_search");
        ToolCallbackProvider provider = providerReturning(orderSearchCallback);
        ToolCatalog catalog = new ToolCatalog(List.of(provider));

        assertThat(catalog.callbackByName("tm_orders_search"))
                .contains(orderSearchCallback);
    }

    @Test
    void shouldReturnEmptyOptionalWhenCallbackWasNotDiscovered() {
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm_orders_search"));
        ToolCatalog catalog = new ToolCatalog(List.of(provider));

        assertThat(catalog.callbackByName("tm_knowledge_search"))
                .isEmpty();
    }

    @Test
    void shouldExposeDiscoveredToolNamesInProviderOrder() {
        ToolCallbackProvider provider = providerReturning(
                callbackNamed("tm_orders_search"),
                callbackNamed("tm_internal_unconfigured")
        );
        ToolCatalog catalog = new ToolCatalog(List.of(provider));

        assertThat(catalog.discoveredToolNames())
                .containsExactly("tm_orders_search", "tm_internal_unconfigured");
    }

    @Test
    void shouldReportDuplicateDiscoveredToolNames() {
        ToolCallbackProvider firstProvider = providerReturning(callbackNamed("tm_orders_search"));
        ToolCallbackProvider secondProvider = providerReturning(
                callbackNamed("tm_orders_search"),
                callbackNamed("tm_orders_search"),
                callbackNamed("tm_knowledge_search")
        );
        ToolCatalog catalog = new ToolCatalog(List.of(firstProvider, secondProvider));

        assertThat(catalog.duplicateDiscoveredToolNames())
                .containsExactly("tm_orders_search");
    }

    private ToolCallbackProvider providerReturning(ToolCallback... callbacks) {
        ToolCallbackProvider provider = mock(ToolCallbackProvider.class);
        when(provider.getToolCallbacks()).thenReturn(callbacks);
        return provider;
    }

    private ToolCallback callbackNamed(String name) {
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        when(toolDefinition.name()).thenReturn(name);

        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition()).thenReturn(toolDefinition);
        return callback;
    }
}
