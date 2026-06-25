package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ObservedToolCallbackTest {

    @Test
    void shouldRecordSuccessfulToolInvocation() {
        ToolCallback delegate = mock(ToolCallback.class);
        ToolDefinition definition = mock(ToolDefinition.class);
        ToolInvocationRecorder recorder = mock(ToolInvocationRecorder.class);
        when(delegate.getToolDefinition()).thenReturn(definition);
        when(definition.name()).thenReturn("tm.orders.search");
        when(delegate.call("{\"status\":\"PAID\"}")).thenReturn("[{\"id\":\"order-1\"}]");

        ObservedToolCallback callback = new ObservedToolCallback("conversation-123", delegate, recorder);

        String result = callback.call("{\"status\":\"PAID\"}");

        assertThat(result).isEqualTo("[{\"id\":\"order-1\"}]");
        verify(recorder).recordSuccess(
                "conversation-123",
                "tm.orders.search",
                "{\"status\":\"PAID\"}",
                "[{\"id\":\"order-1\"}]"
        );
    }

    @Test
    void shouldRecordFailedToolInvocationAndRethrow() {
        ToolCallback delegate = mock(ToolCallback.class);
        ToolDefinition definition = mock(ToolDefinition.class);
        ToolInvocationRecorder recorder = mock(ToolInvocationRecorder.class);
        RuntimeException failure = new RuntimeException("tool unavailable");
        when(delegate.getToolDefinition()).thenReturn(definition);
        when(definition.name()).thenReturn("tm.orders.search");
        when(delegate.call("{}")).thenThrow(failure);

        ObservedToolCallback callback = new ObservedToolCallback("conversation-123", delegate, recorder);

        assertThatThrownBy(() -> callback.call("{}"))
                .isSameAs(failure);
        verify(recorder).recordFailure(
                "conversation-123",
                "tm.orders.search",
                "{}",
                failure
        );
    }
}
