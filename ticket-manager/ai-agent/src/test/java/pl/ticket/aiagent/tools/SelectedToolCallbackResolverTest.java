package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelectedToolCallbackResolverTest {

    @Test
    void shouldResolveCallbacksForSelectedCandidates() {
        ToolCallback orderSearchCallback = callbackNamed("tm.orders.search");
        ToolCallbackProvider provider = providerReturning(orderSearchCallback);
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("tm.orders.search")
        ), CallerContext.anonymous());

        assertThat(resolution.callbacks()).hasSize(1);
        assertThat(resolution.callbacks().get(0)).isNotSameAs(orderSearchCallback);
        assertThat(resolution.callbacks().get(0).getToolDefinition()).isSameAs(orderSearchCallback.getToolDefinition());
        assertThat(resolution.missingCandidates()).isEmpty();
    }

    @Test
    void shouldDelegateProtectedCallbackExecutionWhenPolicyAllowsIt() {
        ToolCallback orderSearchCallback = callbackNamed("tm.orders.search");
        when(orderSearchCallback.call("{\"status\":\"PAID\"}")).thenReturn("orders-json");
        SelectedToolCallbackResolver resolver = resolverWith(providerReturning(orderSearchCallback));

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("tm.orders.search")
        ), CallerContext.anonymous());

        String result = resolution.callbacks().get(0).call("{\"status\":\"PAID\"}");

        assertThat(result).isEqualTo("orders-json");
        verify(orderSearchCallback).call("{\"status\":\"PAID\"}");
    }

    @Test
    void shouldDelegateProtectedCallbackExecutionWithToolContextWhenPolicyAllowsIt() {
        ToolCallback orderSearchCallback = callbackNamed("tm.orders.search");
        ToolContext toolContext = new ToolContext(Map.of("conversationId", "conversation-123"));
        when(orderSearchCallback.call("{}", toolContext)).thenReturn("orders-json");
        SelectedToolCallbackResolver resolver = resolverWith(providerReturning(orderSearchCallback));

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("tm.orders.search")
        ), CallerContext.anonymous());

        String result = resolution.callbacks().get(0).call("{}", toolContext);

        assertThat(result).isEqualTo("orders-json");
        verify(orderSearchCallback).call("{}", toolContext);
    }

    @Test
    void shouldDenyProtectedCallbackExecutionWhenPolicyNoLongerAllowsIt() {
        ToolCallback orderSearchCallback = callbackNamed("tm.orders.search");
        ToolPolicyProperties properties = propertiesWithOrderTool();
        properties.setEnforceScopes(true);
        SelectedToolCallbackResolver resolver = resolverWith(providerReturning(orderSearchCallback), properties);

        ToolCallbackResolution resolution = resolver.resolve(List.of(
                new ToolCandidate("tm.orders.search")
        ), CallerContext.anonymous());

        assertThatThrownBy(() -> resolution.callbacks().get(0).call("{}"))
                .isInstanceOf(ToolExecutionDeniedException.class)
                .hasMessageContaining("tm.orders.search")
                .hasMessageContaining("MISSING_SCOPE");
        verify(orderSearchCallback, never()).call(anyString());
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
        ), CallerContext.anonymous());

        assertThat(resolution.callbacks())
                .extracting(callback -> callback.getToolDefinition().name())
                .containsExactly("first.tool", "second.tool");
    }

    @Test
    void shouldReportMissingCandidates() {
        ToolCallbackProvider provider = providerReturning(callbackNamed("tm.orders.search"));
        SelectedToolCallbackResolver resolver = resolverWith(provider);

        ToolCandidate missingCandidate = new ToolCandidate("missing.tool");
        ToolCallbackResolution resolution = resolver.resolve(List.of(missingCandidate), CallerContext.anonymous());

        assertThat(resolution.callbacks()).isEmpty();
        assertThat(resolution.missingCandidates()).containsExactly(missingCandidate);
    }

    @Test
    void shouldReturnEmptyResolutionWhenNoCandidatesWereSelected() {
        SelectedToolCallbackResolver resolver = resolverWith(providerReturning(callbackNamed("tm.orders.search")));

        ToolCallbackResolution resolution = resolver.resolve(List.<ToolCandidate>of(), CallerContext.anonymous());

        assertThat(resolution.callbacks()).isEmpty();
        assertThat(resolution.missingCandidates()).isEmpty();
    }

    private ToolCallbackProvider providerReturning(ToolCallback... callbacks) {
        ToolCallbackProvider provider = mock(ToolCallbackProvider.class);
        when(provider.getToolCallbacks()).thenReturn(callbacks);
        return provider;
    }

    private SelectedToolCallbackResolver resolverWith(ToolCallbackProvider provider) {
        return resolverWith(provider, propertiesWithOrderTool());
    }

    private SelectedToolCallbackResolver resolverWith(ToolCallbackProvider provider, ToolPolicyProperties properties) {
        return new SelectedToolCallbackResolver(new ToolCatalog(properties, List.of(provider)), new ToolPolicy(properties));
    }

    private ToolCallback callbackNamed(String name) {
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        when(toolDefinition.name()).thenReturn(name);

        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition()).thenReturn(toolDefinition);
        return callback;
    }

    private ToolPolicyProperties propertiesWithOrderTool() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        ToolPolicyProperties.ToolMetadata metadata = metadata();
        metadata.setRequiredScopes(List.of("tools:orders.read"));
        properties.setRegistry(Map.of(
                "tm.orders.search", metadata,
                "first.tool", metadata(),
                "second.tool", metadata()
        ));
        return properties;
    }

    private ToolPolicyProperties.ToolMetadata metadata() {
        ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
        metadata.setSource(ToolSourceType.INTERNAL_MCP);
        metadata.setAccessMode(ToolAccessMode.READ);
        return metadata;
    }
}
