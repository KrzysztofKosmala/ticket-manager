package pl.ticket.aiagent.conversation;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record Conversation(
        String id,
        String ownerSubject,
        List<ConversationMessage> messages
) {

    public Conversation {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Conversation id must not be blank");
        }
        if (!StringUtils.hasText(ownerSubject)) {
            throw new IllegalArgumentException("Conversation owner must not be blank");
        }
        messages = messages == null ? List.of() : List.copyOf(messages);
    }

    public Conversation withMessage(ConversationMessage message) {
        if (message == null) {
            return this;
        }

        List<ConversationMessage> nextMessages = new ArrayList<>(messages);
        nextMessages.add(message);
        return new Conversation(id, ownerSubject, nextMessages);
    }
}
