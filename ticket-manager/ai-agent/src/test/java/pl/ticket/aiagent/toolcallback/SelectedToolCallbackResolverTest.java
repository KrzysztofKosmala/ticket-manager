package pl.ticket.aiagent.toolcallback;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import pl.ticket.aiagent.toolselection.ToolCandidate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectedToolCallbackResolverTest {

    @Test
    void shouldResolveCallbacksForSelectedCandidates() {
        ToolCallback orderSearchCallback = callbackNamed("tm.orders.search");
        ToolCallbackProvider provider = providerReturning(orderSearchCallback);
        SelectedToolCallbackResolver resolver = new SelectedToolCallbackResolver(List.of(provider));

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("tm.orders.search", "Search orders")
        ));

        assertThat(resolution.callbacks()).containsExactly(orderSearchCallback);
        assertThat(resolution.missingCandidates()).isEmpty();
    }

    @Test
    void shouldPreserveCandidateOrder() {
        ToolCallback firstCallback = callbackNamed("first.tool");
        ToolCallback secondCallback = callbackNamed("second.tool");
        ToolCallbackProvider provider = providerReturning(secondCallback, firstCallback);
        SelectedToolCallbackResolver resolver = new SelectedToolCallbackResolver(List.of(provider));

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("first.tool", "First"),
                new ToolCandidate("second.tool", "Second")
        ));

        assertThat(resolution.callbacks()).containsExactly(firstCallback, secondCallback);
    }

    @Test
    void shouldReportMissingCandidates() {
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm.orders.search"));
        SelectedToolCallbackResolver resolver = new SelectedToolCallbackResolver(List.of(provider));

        ToolCandidate missingCandidate = new ToolCandidate("missing.tool", "Missing");
        ToolCallbackResolution resolution = resolver.resolve(List.of(missingCandidate));

        assertThat(resolution.callbacks()).isEmpty();
        assertThat(resolution.missingCandidates()).containsExactly(missingCandidate);
    }

    @Test
    void shouldReturnEmptyResolutionWhenNoCandidatesWereSelected() {
        SelectedToolCallbackResolver resolver = new SelectedToolCallbackResolver(List.of(
                providerReturning(callbackNamed("tm.orders.search"))
        ));

        ToolCallbackResolution resolution = resolver.resolve(List.of());

        assertThat(resolution.callbacks()).isEmpty();
        assertThat(resolution.missingCandidates()).isEmpty();
        assertThat(resolution.hasCallbacks()).isFalse();
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
