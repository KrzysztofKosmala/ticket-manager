package pl.ticket.aiagent.conversation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.security.CallerContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "ai-agent.persistence.mode", havingValue = "jpa")
public class JpaConversationStore implements ConversationStore {

    private final AiConversationJpaRepository conversationRepository;

    public JpaConversationStore(AiConversationJpaRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    @Override
    @Transactional
    public Conversation getOrCreate(String conversationId, CallerContext callerContext) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        if (StringUtils.hasText(conversationId)) {
            Optional<Conversation> existingConversation = findById(conversationId, effectiveCallerContext);
            if (existingConversation.isPresent()) {
                return existingConversation.get();
            }
        }

        AiConversationEntity conversation = new AiConversationEntity(
                UUID.randomUUID().toString(),
                effectiveCallerContext.subject(),
                LocalDateTime.now()
        );
        return conversationRepository.save(conversation).toConversation();
    }

    @Override
    @Transactional
    public Conversation appendMessage(String conversationId, CallerContext callerContext, ConversationMessage message) {
        AiConversationEntity conversation = findOwnedEntity(conversationId, callerContext);
        conversation.appendMessage(message);
        return conversationRepository.save(conversation).toConversation();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findById(String conversationId, CallerContext callerContext) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        return conversationRepository.findByIdAndOwnerSubject(conversationId, effectiveCallerContext.subject())
                .map(AiConversationEntity::toConversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findIds(CallerContext callerContext) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        return conversationRepository.findAllByOwnerSubject(effectiveCallerContext.subject()).stream()
                .map(AiConversationEntity::getId)
                .toList();
    }

    @Override
    @Transactional
    public Conversation replaceMessages(
            String conversationId,
            CallerContext callerContext,
            List<ConversationMessage> messages
    ) {
        AiConversationEntity conversation = findOwnedEntity(conversationId, callerContext);
        conversation.replaceMessages(messages == null ? List.of() : messages);
        return conversationRepository.save(conversation).toConversation();
    }

    @Override
    @Transactional
    public Conversation clearMessages(String conversationId, CallerContext callerContext) {
        return replaceMessages(conversationId, callerContext, List.of());
    }

    private AiConversationEntity findOwnedEntity(String conversationId, CallerContext callerContext) {
        CallerContext effectiveCallerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        return conversationRepository.findByIdAndOwnerSubject(conversationId, effectiveCallerContext.subject())
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found for current caller"));
    }
}
