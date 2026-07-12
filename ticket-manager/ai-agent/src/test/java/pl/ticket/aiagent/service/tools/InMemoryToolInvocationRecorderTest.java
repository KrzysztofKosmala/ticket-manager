package pl.ticket.aiagent.service.tools;

import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.model.tools.ToolInvocation;
import pl.ticket.aiagent.model.tools.ToolInvocationStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryToolInvocationRecorderTest {

    @Test
    void shouldStoreSuccessfulInvocationsByConversationId() {
        InMemoryToolInvocationRecorder recorder = new InMemoryToolInvocationRecorder();

        recorder.recordSuccess("conversation-123", "tm_my_orders_search", "{\"status\":\"PAID\"}", "[{\"id\":\"order-1\"}]");

        assertThat(recorder.findByConversationId("conversation-123"))
                .containsExactly(new ToolInvocation(
                        "conversation-123",
                        null,
                        "tm_my_orders_search",
                        "{\"status\":\"PAID\"}",
                        ToolInvocationStatus.SUCCESS,
                        "[{\"id\":\"order-1\"}]",
                        null
                ));
    }

    @Test
    void shouldStoreFailedInvocationsByConversationId() {
        InMemoryToolInvocationRecorder recorder = new InMemoryToolInvocationRecorder();
        RuntimeException failure = new RuntimeException("tool unavailable");

        recorder.recordFailure("conversation-123", "tm_my_orders_search", "{}", failure);

        assertThat(recorder.findByConversationId("conversation-123"))
                .containsExactly(new ToolInvocation(
                        "conversation-123",
                        null,
                        "tm_my_orders_search",
                        "{}",
                        ToolInvocationStatus.FAILED,
                        null,
                        "tool unavailable"
                ));
    }

    @Test
    void shouldReturnEmptyListForUnknownConversation() {
        InMemoryToolInvocationRecorder recorder = new InMemoryToolInvocationRecorder();

        List<ToolInvocation> invocations = recorder.findByConversationId("missing");

        assertThat(invocations).isEmpty();
    }
}
