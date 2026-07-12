package pl.ticket.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaToolInvocationRecorder.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "ai-agent.persistence.mode=jpa")
class JpaToolInvocationRecorderTest {

    @Autowired
    private JpaToolInvocationRecorder recorder;

    @Test
    void shouldPersistSuccessfulInvocationWithArgumentsAndResult() {
        recorder.recordSuccess(
                "conversation-123",
                "tm_my_order_payment_status_get",
                "{\"orderId\":1001}",
                "{\"orderId\":1001,\"status\":\"PAID\"}"
        );

        assertThat(recorder.findByConversationId("conversation-123"))
                .containsExactly(new ToolInvocation(
                        "conversation-123",
                        null,
                        "tm_my_order_payment_status_get",
                        "{\"orderId\":1001}",
                        ToolInvocationStatus.SUCCESS,
                        "{\"orderId\":1001,\"status\":\"PAID\"}",
                        null
                ));
    }

    @Test
    void shouldPersistFailedInvocationWithArgumentsAndErrorMessage() {
        recorder.recordFailure(
                "conversation-123",
                "tm_event_capacity_check",
                "null",
                new RuntimeException("tool timeout")
        );

        assertThat(recorder.findByConversationId("conversation-123"))
                .containsExactly(new ToolInvocation(
                        "conversation-123",
                        null,
                        "tm_event_capacity_check",
                        "null",
                        ToolInvocationStatus.FAILED,
                        null,
                        "tool timeout"
                ));
    }
}
