package pl.ticket.aiagent.service.conversation;

import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.model.conversation.ConversationMessage;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;

import java.util.List;
import java.util.Optional;

@Component
public class ConversationChatMemoryRepository implements ChatMemoryRepository {

    private final ConversationStore conversationStore;
    private final CallerContextProvider callerContextProvider;

    public ConversationChatMemoryRepository(
            ConversationStore conversationStore,
            CallerContextProvider callerContextProvider
    ) {
        this.conversationStore = conversationStore;
        this.callerContextProvider = callerContextProvider;
    }

    @Override
    public List<String> findConversationIds() {
        return conversationStore.findIds(currentCaller());
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return conversationStore.findById(conversationId, currentCaller())
                .map(Conversation::messages)
                .orElse(List.of())
                .stream()
                .map(this::toSpringAiMessage)
                .toList();
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        List<ConversationMessage> conversationMessages = messages.stream()
                .map(this::toConversationMessage)
                .flatMap(Optional::stream)
                .toList();

        conversationStore.replaceMessages(conversationId, currentCaller(), conversationMessages);
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        conversationStore.clearMessages(conversationId, currentCaller());
    }

    private CallerContext currentCaller() {
        return callerContextProvider.current();
    }

    private Message toSpringAiMessage(ConversationMessage message) {
        return switch (message.role()) {
            case USER -> new UserMessage(message.content());
            case ASSISTANT -> new AssistantMessage(message.content());
        };
    }

    private Optional<ConversationMessage> toConversationMessage(Message message) {
        if (message == null || !StringUtils.hasText(message.getText())) {
            return Optional.empty();
        }
        if (message.getMessageType() == MessageType.USER) {
            return Optional.of(ConversationMessage.user(message.getText()));
        }
        if (message.getMessageType() == MessageType.ASSISTANT) {
            return Optional.of(ConversationMessage.assistant(message.getText()));
        }
        return Optional.empty();
    }
}
