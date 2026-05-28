package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectedToolCallbackResolverTest {

    @Test
    void shouldResolveCallbacksForSelectedCandidates() {
        ToolCallback orderSearchCallback = callbackNamed("tm.orders.search");
        ToolCallbackProvider provider = providerReturning(orderSearchCallback);
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("tm.orders.search")
        ));

        assertThat(resolution.callbacks()).containsExactly(orderSearchCallback);
        assertThat(resolution.missingCandidates()).isEmpty();
    }

    @Test
    void shouldPreserveCandidateOrder() {
        ToolCallback firstCallback = callbackNamed("first.tool");
        ToolCallback secondCallback = callbackNamed("second.tool");
        ToolCallbackProvider provider = providerReturning(secondCallback, firstCallback);
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("first.tool"),
                new ToolCandidate("second.tool")
        ));

        assertThat(resolution.callbacks()).containsExactly(firstCallback, secondCallback);
    }

    @Test
    void shouldReportMissingCandidates() {
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm.orders.search"));
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        ToolCandidate missingCandidate = new ToolCandidate("missing.tool");
        ToolCallbackResolution resolution = resolver.resolve(List.of(missingCandidate));

        assertThat(resolution.callbacks()).isEmpty();
        assertThat(resolution.missingCandidates()).containsExactly(missingCandidate);
    }

    @Test
    void shouldReturnEmptyResolutionWhenNoCandidatesWereSelected() {
        SelectedToolCallbackResolver resolver = resolverWith(providerReturning(callbackNamed("tm.orders.search")));

        ToolCallbackResolution resolution = resolver.resolve(List.of());

        assertThat(resolution.callbacks()).isEmpty();
        assertThat(resolution.missingCandidates()).isEmpty();
    }

    private ToolCallbackProvider providerReturning(ToolCallback... callbacks) {
        ToolCallbackProvider provider = mock(ToolCallbackProvider.class);
        when(provider.getToolCallbacks()).thenReturn(callbacks);
        return provider;
    }

    private SelectedToolCallbackResolver resolverWith(ToolCallbackProvider provider) {
        return new SelectedToolCallbackResolver(new ToolCatalog(new ToolPolicyProperties(), List.of(provider)));
    }

    private ToolCallback callbackNamed(String name) {
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        when(toolDefinition.name()).thenReturn(name);

        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition()).thenReturn(toolDefinition);
        return callback;
    }
}
