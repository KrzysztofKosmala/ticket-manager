package pl.ticket.aiagent.service.conversation;

import pl.ticket.aiagent.model.conversation.Conversation;
import pl.ticket.aiagent.model.conversation.ConversationMessage;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;
import java.util.Optional;

public interface ConversationStore {

    Conversation getOrCreate(String conversationId, CallerContext callerContext);

    Conversation appendMessage(String conversationId, CallerContext callerContext, ConversationMessage message);

    Optional<Conversation> findById(String conversationId, CallerContext callerContext);

    List<String> findIds(CallerContext callerContext);

    Conversation replaceMessages(
            String conversationId,
            CallerContext callerContext,
            List<ConversationMessage> messages
    );

    Conversation clearMessages(String conversationId, CallerContext callerContext);
}
