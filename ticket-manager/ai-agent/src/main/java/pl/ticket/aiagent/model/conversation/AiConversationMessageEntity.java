package pl.ticket.aiagent.model.conversation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_conversation_message")
public class AiConversationMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_id")
    private AiConversationEntity conversation;
    @Enumerated(EnumType.STRING)
    private ConversationMessageRole role;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer messageOrder;
    private LocalDateTime createdAt;

    protected AiConversationMessageEntity() {
    }

    private AiConversationMessageEntity(
            AiConversationEntity conversation,
            ConversationMessageRole role,
            String content,
            Integer messageOrder
    ) {
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.messageOrder = messageOrder;
        this.createdAt = LocalDateTime.now();
    }

    public static AiConversationMessageEntity from(
            AiConversationEntity conversation,
            ConversationMessage message,
            Integer position
    ) {
        return new AiConversationMessageEntity(conversation, message.role(), message.content(), position);
    }

    public ConversationMessage toConversationMessage() {
        return new ConversationMessage(role, content);
    }
}
