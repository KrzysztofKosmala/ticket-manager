package pl.ticket.aiagent.service.conversation;

import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.model.conversation.ConversationMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConversationStore.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "ai-agent.persistence.mode=jpa")
class JpaConversationStoreTest {

    @Autowired
    private JpaConversationStore store;

    @Test
    void shouldPersistMessagesForOwnedConversation() {
        CallerContext callerContext = caller("user-123");

        Conversation conversation = store.getOrCreate(null, callerContext);
        store.appendMessage(conversation.id(), callerContext, ConversationMessage.user("Czy zamowienie 1001 jest oplacone?"));
        Conversation updatedConversation = store.appendMessage(
                conversation.id(),
                callerContext,
                ConversationMessage.assistant("Tak, zamowienie 1001 jest oplacone.")
        );

        assertThat(updatedConversation.messages()).containsExactly(
                ConversationMessage.user("Czy zamowienie 1001 jest oplacone?"),
                ConversationMessage.assistant("Tak, zamowienie 1001 jest oplacone.")
        );
        assertThat(store.findById(conversation.id(), callerContext))
                .contains(updatedConversation);
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

    private CallerContext caller(String subject) {
        return new CallerContext(subject, Set.of(), Set.of());
    }
}
