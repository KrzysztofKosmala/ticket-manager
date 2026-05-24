package pl.ticket.aiagent.toolcatalog;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import pl.ticket.aiagent.toolpolicy.ToolPolicyProperties;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToolCatalogTest {

    @Test
    void shouldExposeConfiguredToolNamesInOrder() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setAllowList(List.of("tm.orders.search", "tm.knowledge.search"));
        ToolCatalog catalog = new ToolCatalog(properties, List.of());

        assertThat(catalog.configuredToolNames())
                .containsExactly("tm.orders.search", "tm.knowledge.search");
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
