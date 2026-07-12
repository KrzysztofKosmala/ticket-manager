package pl.ticket.aiagent.conversation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@ConditionalOnProperty(name = "ai-agent.persistence.mode", havingValue = "memory", matchIfMissing = true)
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

    @Override
    public Optional<Conversation> findById(String conversationId, CallerContext callerContext) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        Conversation conversation = conversations.get(conversationId);
        if (!isOwnedBy(conversation, effectiveCallerContext)) {
            return Optional.empty();
        }
        return Optional.of(conversation);
    }

    @Override
    public List<String> findIds(CallerContext callerContext) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        return conversations.values().stream()
                .filter(conversation -> isOwnedBy(conversation, effectiveCallerContext))
                .map(Conversation::id)
                .toList();
    }

    @Override
    public Conversation replaceMessages(
            String conversationId,
            CallerContext callerContext,
            List<ConversationMessage> messages
    ) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        return conversations.compute(conversationId, (id, existingConversation) -> {
            if (!isOwnedBy(existingConversation, effectiveCallerContext)) {
                throw new IllegalArgumentException("Conversation not found for current caller");
            }
            return new Conversation(id, existingConversation.ownerSubject(), messages);
        });
    }

    @Override
    public Conversation clearMessages(String conversationId, CallerContext callerContext) {
        return replaceMessages(conversationId, callerContext, List.of());
    }

    private boolean isOwnedBy(Conversation conversation, CallerContext callerContext) {
        return conversation != null && conversation.ownerSubject().equals(callerContext.subject());
    }
}
