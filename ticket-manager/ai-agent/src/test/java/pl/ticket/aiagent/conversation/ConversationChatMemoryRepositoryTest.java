package pl.ticket.aiagent.conversation;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConversationChatMemoryRepositoryTest {

    private static final CallerContext CALLER = new CallerContext("user-123", Set.of(), Set.of());

    private final InMemoryConversationStore conversationStore = new InMemoryConversationStore();
    private final CallerContextProvider callerContextProvider = mock(CallerContextProvider.class);
    private final ConversationChatMemoryRepository repository =
            new ConversationChatMemoryRepository(conversationStore, callerContextProvider);

    @Test
    void shouldReadConversationMessagesAsSpringAiMessages() {
        when(callerContextProvider.current()).thenReturn(CALLER);
        Conversation conversation = conversationStore.getOrCreate(null, CALLER);
        conversationStore.appendMessage(conversation.id(), CALLER, ConversationMessage.user("Czesc"));
        conversationStore.appendMessage(conversation.id(), CALLER, ConversationMessage.assistant("Hej"));

        List<Message> messages = repository.findByConversationId(conversation.id());

        assertThat(messages)
                .containsExactly(new UserMessage("Czesc"), new AssistantMessage("Hej"));
    }

    @Test
    void shouldReplaceConversationMessagesFromSpringAiMessages() {
        when(callerContextProvider.current()).thenReturn(CALLER);
        Conversation conversation = conversationStore.getOrCreate(null, CALLER);
        conversationStore.appendMessage(conversation.id(), CALLER, ConversationMessage.user("Stara wiadomosc"));

        repository.saveAll(
                conversation.id(),
                List.of(new UserMessage("Nowa wiadomosc"), new AssistantMessage("Nowa odpowiedz"))
        );

        assertThat(conversationStore.findById(conversation.id(), CALLER).orElseThrow().messages())
                .containsExactly(
                        ConversationMessage.user("Nowa wiadomosc"),
                        ConversationMessage.assistant("Nowa odpowiedz")
                );
    }

    @Test
    void shouldListConversationIdsForCurrentCaller() {
        when(callerContextProvider.current()).thenReturn(CALLER);
        Conversation conversation = conversationStore.getOrCreate(null, CALLER);
        conversationStore.getOrCreate(null, new CallerContext("other-user", Set.of(), Set.of()));

        assertThat(repository.findConversationIds()).containsExactly(conversation.id());
    }

    @Test
    void shouldClearConversationMessages() {
        when(callerContextProvider.current()).thenReturn(CALLER);
        Conversation conversation = conversationStore.getOrCreate(null, CALLER);
        conversationStore.appendMessage(conversation.id(), CALLER, ConversationMessage.user("Czesc"));

        repository.deleteByConversationId(conversation.id());

        assertThat(conversationStore.findById(conversation.id(), CALLER).orElseThrow().messages()).isEmpty();
    }
}
