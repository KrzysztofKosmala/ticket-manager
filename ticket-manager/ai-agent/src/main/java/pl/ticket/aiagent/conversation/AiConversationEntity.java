package pl.ticket.aiagent.conversation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_conversation")
public class AiConversationEntity {

    @Id
    private String id;
    private String ownerSubject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("messageOrder ASC")
    private List<AiConversationMessageEntity> messages = new ArrayList<>();

    protected AiConversationEntity() {
    }

    public AiConversationEntity(String id, String ownerSubject, LocalDateTime now) {
        this.id = id;
        this.ownerSubject = ownerSubject;
        this.createdAt = now;
        this.updatedAt = now;
        this.status = "ACTIVE";
    }

    public String getId() {
        return id;
    }

    public String getOwnerSubject() {
        return ownerSubject;
    }

    public List<AiConversationMessageEntity> getMessages() {
        return messages;
    }

    public void appendMessage(ConversationMessage message) {
        messages.add(AiConversationMessageEntity.from(this, message, messages.size()));
        touch();
    }

    public void replaceMessages(List<ConversationMessage> nextMessages) {
        messages.clear();
        for (int index = 0; index < nextMessages.size(); index++) {
            messages.add(AiConversationMessageEntity.from(this, nextMessages.get(index), index));
        }
        touch();
    }

    public Conversation toConversation() {
        return new Conversation(
                id,
                ownerSubject,
                messages.stream()
                        .map(AiConversationMessageEntity::toConversationMessage)
                        .toList()
        );
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }
}
