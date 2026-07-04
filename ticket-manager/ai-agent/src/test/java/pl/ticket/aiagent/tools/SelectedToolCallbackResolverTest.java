package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectedToolCallbackResolverTest {

    @Test
    void shouldResolveCallbacksForSelectedCandidates() {
        ToolCallback orderSearchCallback = callbackNamed("tm_orders_search");
        ToolCallbackProvider provider = providerReturning(orderSearchCallback);
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        List<ToolCallback> callbacks = resolver.resolve(List.of(
                new ToolCandidate("tm_orders_search")
        ));

        assertThat(callbacks).containsExactly(orderSearchCallback);
    }

    @Test
    void shouldPreserveCandidateOrder() {
        ToolCallback firstCallback = callbackNamed("first_tool");
        ToolCallback secondCallback = callbackNamed("second_tool");
        ToolCallbackProvider provider = providerReturning(secondCallback, firstCallback);
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        List<ToolCallback> callbacks = resolver.resolve(List.of(
                new ToolCandidate("first_tool"),
                new ToolCandidate("second_tool")
        ));

        assertThat(callbacks).containsExactly(firstCallback, secondCallback);
    }

    @Test
    void shouldThrowWhenSelectedCandidateWasNotDiscovered() {
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm_orders_search"));
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        ToolCandidate missingCandidate = new ToolCandidate("missing_tool");

        assertThatThrownBy(() -> resolver.resolve(List.of(missingCandidate)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Selected tool callback was not discovered: missing_tool");
    }

    @Test
    void shouldReturnEmptyListWhenNoCandidatesWereSelected() {
        SelectedToolCallbackResolver resolver = resolverWith(providerReturning(callbackNamed("tm_orders_search")));

        List<ToolCallback> callbacks = resolver.resolve(List.of());

        assertThat(callbacks).isEmpty();
    }

    private ToolCallbackProvider providerReturning(ToolCallback... callbacks) {
        ToolCallbackProvider provider = mock(ToolCallbackProvider.class);
        when(provider.getToolCallbacks()).thenReturn(callbacks);
        return provider;
    }

    private SelectedToolCallbackResolver resolverWith(ToolCallbackProvider provider) {
        return new SelectedToolCallbackResolver(new ToolCatalog(List.of(provider)));
    }

    private ToolCallback callbackNamed(String name) {
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        when(toolDefinition.name()).thenReturn(name);

        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition()).thenReturn(toolDefinition);
        return callback;
    }
}
