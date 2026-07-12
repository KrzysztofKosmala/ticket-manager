package pl.ticket.aiagent.model.conversation;

import org.springframework.util.StringUtils;

public record ConversationMessage(
        ConversationMessageRole role,
        String content
) {

    public ConversationMessage {
        if (role == null) {
            throw new IllegalArgumentException("Conversation message role must not be null");
        }
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("Conversation message content must not be blank");
        }
    }

    public static ConversationMessage user(String content) {
        return new ConversationMessage(ConversationMessageRole.USER, content);
    }

    public static ConversationMessage assistant(String content) {
        return new ConversationMessage(ConversationMessageRole.ASSISTANT, content);
    }
}
