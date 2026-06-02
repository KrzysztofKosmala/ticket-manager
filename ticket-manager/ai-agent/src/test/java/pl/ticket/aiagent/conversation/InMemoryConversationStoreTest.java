package pl.ticket.aiagent.conversation;

import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.security.CallerContext;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryConversationStoreTest {

    private final InMemoryConversationStore store = new InMemoryConversationStore();

    @Test
    void shouldCreateConversationForCurrentCaller() {
        CallerContext callerContext = caller("user-123");

        Conversation conversation = store.getOrCreate(null, callerContext);

        assertThat(conversation.id()).isNotBlank();
        assertThat(conversation.ownerSubject()).isEqualTo("user-123");
        assertThat(conversation.messages()).isEmpty();
    }

    @Test
    void shouldAppendMessagesToOwnedConversation() {
        CallerContext callerContext = caller("user-123");
        Conversation conversation = store.getOrCreate(null, callerContext);

        store.appendMessage(conversation.id(), callerContext, ConversationMessage.user("Czesc"));
        Conversation updatedConversation = store.appendMessage(
                conversation.id(),
                callerContext,
                ConversationMessage.assistant("Czesc, jak moge pomoc?")
        );

        assertThat(updatedConversation.messages()).containsExactly(
                ConversationMessage.user("Czesc"),
                ConversationMessage.assistant("Czesc, jak moge pomoc?")
        );
    }

    @Test
    void shouldCreateNewConversationWhenRequestedConversationBelongsToAnotherCaller() {
        Conversation firstConversation = store.getOrCreate(null, caller("user-123"));

        Conversation secondConversation = store.getOrCreate(firstConversation.id(), caller("user-456"));

        assertThat(secondConversation.id()).isNotEqualTo(firstConversation.id());
        assertThat(secondConversation.ownerSubject()).isEqualTo("user-456");
    }

    private CallerContext caller(String subject) {
        return new CallerContext(subject, Set.of(), Set.of());
    }
}
