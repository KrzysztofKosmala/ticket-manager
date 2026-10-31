package pl.ticket.aiagent.configuration.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.service.conversation.ConversationChatMemoryRepository;
import pl.ticket.aiagent.model.conversation.ConversationMessage;
import pl.ticket.aiagent.service.conversation.ConversationStore;
import pl.ticket.aiagent.service.conversation.InMemoryConversationStore;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringAiChatMemoryConfigTest {

    private static final CallerContext CALLER = new CallerContext("user-123", Set.of(), Set.of());

    private final InMemoryConversationStore conversationStore = new InMemoryConversationStore();
    private final CallerContextProvider callerContextProvider = mock(CallerContextProvider.class);
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(ConversationStore.class, () -> conversationStore)
            .withBean(CallerContextProvider.class, () -> callerContextProvider)
            .withUserConfiguration(SpringAiChatMemoryConfig.class, ConversationChatMemoryRepository.class);

    @Test
    void shouldExposeWindowChatMemoryForSpringAiAdvisors() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ChatMemory.class);
            assertThat(context.getBean(ChatMemory.class)).isInstanceOf(MessageWindowChatMemory.class);
        });
    }

    @Test
    void shouldExposeMessageChatMemoryAdvisor() {
        contextRunner.run(context ->
                assertThat(context).hasSingleBean(MessageChatMemoryAdvisor.class)
        );
    }

    @Test
    void shouldBackSpringAiChatMemoryWithConversationStore() {
        when(callerContextProvider.current()).thenReturn(CALLER);
        Conversation conversation = conversationStore.getOrCreate(null, CALLER);

        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ChatMemoryRepository.class);

            context.getBean(ChatMemory.class).add(conversation.id(), new UserMessage("Czesc"));

            assertThat(conversationStore.findById(conversation.id(), CALLER).orElseThrow().messages())
                    .containsExactly(ConversationMessage.user("Czesc"));
        });
    }
}
