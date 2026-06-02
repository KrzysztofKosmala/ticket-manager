package pl.ticket.aiagent.conversation;

import pl.ticket.aiagent.security.CallerContext;

public interface ConversationStore {

    Conversation getOrCreate(String conversationId, CallerContext callerContext);

    Conversation appendMessage(String conversationId, CallerContext callerContext, ConversationMessage message);
}
