package pl.ticket.aiagent.conversation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryConversationStore implements ConversationStore {

    private final ConcurrentMap<String, Conversation> conversations = new ConcurrentHashMap<>();

    @Override
    public Conversation getOrCreate(String conversationId, CallerContext callerContext) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        if (StringUtils.hasText(conversationId)) {
            Conversation existingConversation = conversations.get(conversationId);
            if (isOwnedBy(existingConversation, effectiveCallerContext)) {
                return existingConversation;
            }
        }

        Conversation conversation = new Conversation(
                UUID.randomUUID().toString(),
                effectiveCallerContext.subject(),
                List.of()
        );
        conversations.put(conversation.id(), conversation);
        return conversation;
    }

    @Override
    public Conversation appendMessage(String conversationId, CallerContext callerContext, ConversationMessage message) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        return conversations.compute(conversationId, (id, existingConversation) -> {
            if (!isOwnedBy(existingConversation, effectiveCallerContext)) {
                throw new IllegalArgumentException("Conversation not found for current caller");
            }
            return existingConversation.withMessage(message);
        });
    }

    private boolean isOwnedBy(Conversation conversation, CallerContext callerContext) {
        return conversation != null && conversation.ownerSubject().equals(callerContext.subject());
    }
}
