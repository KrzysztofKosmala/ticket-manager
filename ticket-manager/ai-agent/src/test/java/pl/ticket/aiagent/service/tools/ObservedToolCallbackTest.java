package pl.ticket.aiagent.service.tools;

import pl.ticket.aiagent.model.conversation.Conversation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class ObservedToolCallbackTest {

    @Test
    void shouldRecordSuccessfulToolInvocation() {
        ToolCallback originalCallback = mock(ToolCallback.class);
        ToolDefinition definition = mock(ToolDefinition.class);
        ToolInvocationRecorder recorder = mock(ToolInvocationRecorder.class);
        when(originalCallback.getToolDefinition()).thenReturn(definition);
        when(definition.name()).thenReturn("tm_my_orders_search");
        when(originalCallback.call("{\"status\":\"PAID\"}")).thenReturn("[{\"id\":\"order-1\"}]");

        ObservedToolCallback callback = new ObservedToolCallback("conversation-123", originalCallback, recorder);

        String result = callback.call("{\"status\":\"PAID\"}");

        assertThat(result).isEqualTo("[{\"id\":\"order-1\"}]");
        verify(recorder).recordSuccess(
                "conversation-123",
                null,
                "tm_my_orders_search",
                "{\"status\":\"PAID\"}",
                "[{\"id\":\"order-1\"}]"
        );
    }

    @Test
    void shouldLogToolInvocationLifecycle(CapturedOutput output) {
        ToolCallback originalCallback = mock(ToolCallback.class);
        ToolDefinition definition = mock(ToolDefinition.class);
        ToolInvocationRecorder recorder = mock(ToolInvocationRecorder.class);
        when(originalCallback.getToolDefinition()).thenReturn(definition);
        when(definition.name()).thenReturn("tm_my_orders_search");
        when(originalCallback.call("{}")).thenReturn("{\"totalCount\":0}");

        ObservedToolCallback callback = new ObservedToolCallback("conversation-123", originalCallback, recorder);

        callback.call("{}");

        assertThat(output)
                .contains("AI tool invocation started: conversationId=conversation-123, toolName=tm_my_orders_search")
                .contains("AI tool invocation succeeded: conversationId=conversation-123, toolName=tm_my_orders_search");
    }

    @Test
    void shouldRecordFailedToolInvocationAndRethrow() {
        ToolCallback originalCallback = mock(ToolCallback.class);
        ToolDefinition definition = mock(ToolDefinition.class);
        ToolInvocationRecorder recorder = mock(ToolInvocationRecorder.class);
        RuntimeException failure = new RuntimeException("tool unavailable");
        when(originalCallback.getToolDefinition()).thenReturn(definition);
        when(definition.name()).thenReturn("tm_my_orders_search");
        when(originalCallback.call("{}")).thenThrow(failure);

        ObservedToolCallback callback = new ObservedToolCallback("conversation-123", originalCallback, recorder);

        assertThatThrownBy(() -> callback.call("{}"))
                .isSameAs(failure);
        verify(recorder).recordFailure(
                "conversation-123",
                null,
                "tm_my_orders_search",
                "{}",
                failure
        );
    }
}
