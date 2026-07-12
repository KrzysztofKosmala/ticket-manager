package pl.ticket.aiagent.service.conversation;

import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.model.conversation.ConversationMessage;
import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;
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

    @Test
    void shouldFindOwnedConversationById() {
        CallerContext callerContext = caller("user-123");
        Conversation conversation = store.getOrCreate(null, callerContext);

        assertThat(store.findById(conversation.id(), callerContext)).contains(conversation);
    }

    @Test
    void shouldReplaceMessagesInOwnedConversation() {
        CallerContext callerContext = caller("user-123");
        Conversation conversation = store.getOrCreate(null, callerContext);
        store.appendMessage(conversation.id(), callerContext, ConversationMessage.user("Stara wiadomosc"));

        Conversation updatedConversation = store.replaceMessages(
                conversation.id(),
                callerContext,
                List.of(ConversationMessage.user("Nowa wiadomosc"))
        );

        assertThat(updatedConversation.messages()).containsExactly(ConversationMessage.user("Nowa wiadomosc"));
    }

    @Test
    void shouldClearMessagesInOwnedConversation() {
        CallerContext callerContext = caller("user-123");
        Conversation conversation = store.getOrCreate(null, callerContext);
        store.appendMessage(conversation.id(), callerContext, ConversationMessage.user("Czesc"));

        Conversation updatedConversation = store.clearMessages(conversation.id(), callerContext);

        assertThat(updatedConversation.messages()).isEmpty();
    }

    @Test
    void shouldListConversationIdsForCurrentCaller() {
        CallerContext firstCaller = caller("user-123");
        CallerContext secondCaller = caller("user-456");
        Conversation firstConversation = store.getOrCreate(null, firstCaller);
        Conversation secondConversation = store.getOrCreate(null, firstCaller);
        store.getOrCreate(null, secondCaller);

        assertThat(store.findIds(firstCaller))
                .containsExactlyInAnyOrder(firstConversation.id(), secondConversation.id());
    }

    private CallerContext caller(String subject) {
        return new CallerContext(subject, Set.of(), Set.of());
    }
}
