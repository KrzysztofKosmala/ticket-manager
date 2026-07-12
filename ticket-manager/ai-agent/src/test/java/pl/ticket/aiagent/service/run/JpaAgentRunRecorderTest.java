package pl.ticket.aiagent.service.run;

import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.model.run.AiAgentRunEntity;
import pl.ticket.aiagent.repository.run.AiAgentRunJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pl.ticket.aiagent.model.conversation.AiConversationEntity;
import pl.ticket.aiagent.repository.conversation.AiConversationJpaRepository;
import pl.ticket.aiagent.model.tools.ToolCandidate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaAgentRunRecorder.class, ObjectMapper.class})
@ActiveProfiles("test")
@TestPropertySource(properties = "ai-agent.persistence.mode=jpa")
class JpaAgentRunRecorderTest {

    @Autowired
    private JpaAgentRunRecorder recorder;

    @Autowired
    private AiAgentRunJpaRepository runRepository;

    @Autowired
    private AiConversationJpaRepository conversationRepository;

    @Test
    void shouldPersistStartedAndCompletedRun() {
        conversationRepository.save(new AiConversationEntity("conversation-123", "anonymous", LocalDateTime.now()));

        String runId = recorder.start(
                "conversation-123",
                "Czy zamowienie 1001 jest oplacone?",
                List.of(new ToolCandidate("tm_my_order_payment_status_get"))
        );
        recorder.complete(runId, "Zamowienie 1001 jest oplacone.");

        AiAgentRunEntity run = runRepository.findById(runId).orElseThrow();
        assertThat(run.getStatus()).isEqualTo("COMPLETED");
        assertThat(run.getSelectedToolsJson()).isEqualTo("[\"tm_my_order_payment_status_get\"]");
        assertThat(run.getAnswer()).isEqualTo("Zamowienie 1001 jest oplacone.");
    }

    @Test
    void shouldPersistFailedRun() {
        conversationRepository.save(new AiConversationEntity("conversation-123", "anonymous", LocalDateTime.now()));

        String runId = recorder.start("conversation-123", "Pytanie", List.of());
        recorder.fail(runId, new RuntimeException("model unavailable"));

        AiAgentRunEntity run = runRepository.findById(runId).orElseThrow();
        assertThat(run.getStatus()).isEqualTo("FAILED");
        assertThat(run.getErrorMessage()).isEqualTo("model unavailable");
    }
}
