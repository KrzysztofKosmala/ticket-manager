package pl.ticket.aiagent.service.tools;

import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.model.tools.ToolInvocation;
import pl.ticket.aiagent.model.tools.ToolInvocationStatus;
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
                "tm_my_orders_search",
                "{\"limit\":10}",
                "{\"items\":[]}"
        );

        assertThat(recorder.findByConversationId("conversation-123"))
                .containsExactly(new ToolInvocation(
                        "conversation-123",
                        null,
                        "tm_my_orders_search",
                        "{\"limit\":10}",
                        ToolInvocationStatus.SUCCESS,
                        "{\"items\":[]}",
                        null
                ));
    }

    @Test
    void shouldPersistFailedInvocationWithArgumentsAndErrorMessage() {
        recorder.recordFailure(
                "conversation-123",
                "tm_events_search",
                "null",
                new RuntimeException("tool timeout")
        );

        assertThat(recorder.findByConversationId("conversation-123"))
                .containsExactly(new ToolInvocation(
                        "conversation-123",
                        null,
                        "tm_events_search",
                        "null",
                        ToolInvocationStatus.FAILED,
                        null,
                        "tool timeout"
                ));
    }
}
