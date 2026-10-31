package pl.ticket.aievals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.ticket.aievals.support.AgentClient;
import pl.ticket.aievals.support.AgentClient.AskResponse;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationMemoryIT {

    @Test
    @DisplayName("Agent recalls a marker from the previous turn in the same conversation")
    void shouldRecallMarkerFromPreviousTurn() throws Exception {
        AgentClient agent = AgentClient.fromConfiguration();
        String marker = "EVAL-" + UUID.randomUUID().toString().substring(0, 8);
        long startedAt = System.nanoTime();

        AskResponse first = agent.ask(
                "Zapamietaj oznaczenie testowe tej rozmowy: " + marker
                        + ". To oznaczenie rozmowy, nie numer zamowienia. Potwierdz krotko.",
                null);
        System.out.printf("First response: %s%n", first);
        assertThat(first.status()).as("First turn status").isEqualTo("COMPLETED");
        assertThat(first.answer()).as("First turn answer").isNotBlank();
        assertThat(first.conversationId()).as("New conversation ID").isNotBlank();

        // Only the conversation ID links the turns; the second question does not contain the marker.
        AskResponse second = agent.ask(
                "Jakie oznaczenie testowe podalem w poprzedniej wiadomosci? Zwroc samo oznaczenie.",
                first.conversationId());
        System.out.printf("Second response: %s%nExpected marker: %s%nElapsed millis: %d%n",
                second, marker, Duration.ofNanos(System.nanoTime() - startedAt).toMillis());

        assertThat(second.status()).as("Second turn status").isEqualTo("COMPLETED");
        assertThat(second.conversationId()).as("Conversation continuity").isEqualTo(first.conversationId());
        assertThat(second.answer()).as("Recall of the marker from conversation history").contains(marker);
    }
}
