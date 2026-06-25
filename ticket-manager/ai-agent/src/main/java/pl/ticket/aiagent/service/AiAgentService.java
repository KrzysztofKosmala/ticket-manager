package pl.ticket.aiagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import pl.ticket.aiagent.conversation.Conversation;
import pl.ticket.aiagent.conversation.ConversationStore;
import pl.ticket.aiagent.dto.AiAgentResponse;
import pl.ticket.aiagent.exception.AiModelEmptyResponseException;
import pl.ticket.aiagent.exception.AiModelUnavailableException;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;
import pl.ticket.aiagent.tools.ObservedToolCallback;
import pl.ticket.aiagent.tools.SelectedToolCallbackResolver;
import pl.ticket.aiagent.tools.ToolCallbackResolution;
import pl.ticket.aiagent.tools.ToolCandidate;
import pl.ticket.aiagent.tools.ToolCandidateSelector;
import pl.ticket.aiagent.tools.ToolInvocationRecorder;

import java.util.List;

@Service
public class AiAgentService
{
    private final ChatClient chatClient;
    private final ToolCandidateSelector toolCandidateSelector;
    private final SelectedToolCallbackResolver toolCallbackResolver;
    private final ToolInvocationRecorder toolInvocationRecorder;
    private final CallerContextProvider callerContextProvider;
    private final ConversationStore conversationStore;

    public AiAgentService(
            ChatClient.Builder chatClientBuilder,
            AiAgentInstructions instructions,
            MessageChatMemoryAdvisor chatMemoryAdvisor,
            ToolCandidateSelector toolCandidateSelector,
            SelectedToolCallbackResolver toolCallbackResolver,
            ToolInvocationRecorder toolInvocationRecorder,
            CallerContextProvider callerContextProvider,
            ConversationStore conversationStore
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem(instructions.systemPrompt())
                .defaultAdvisors(chatMemoryAdvisor)
                .build();
        this.toolCandidateSelector = toolCandidateSelector;
        this.toolCallbackResolver = toolCallbackResolver;
        this.toolInvocationRecorder = toolInvocationRecorder;
        this.callerContextProvider = callerContextProvider;
        this.conversationStore = conversationStore;
    }

    public AiAgentResponse ask(String userMessage) {
        return ask(userMessage, null);
    }

    public AiAgentResponse ask(String userMessage, String conversationId) {
        CallerContext callerContext = callerContextProvider.current();
        Conversation conversation = conversationStore.getOrCreate(conversationId, callerContext);

        List<ToolCandidate> selectedTools = toolCandidateSelector.selectFor(userMessage, callerContext);
        ToolCallbackResolution callbackResolution = toolCallbackResolver.resolve(selectedTools);
        List<ToolCallback> observedCallbacks = callbackResolution.callbacks().stream()
                .map(callback -> observeToolCallback(conversation.id(), callback))
                .toList();

        String answer;
        try {
            answer = chatClient.prompt()
                    .user(userMessage)
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversation.id()))
                    .toolCallbacks(observedCallbacks)
                    .call()
                    .content();
        } catch (TransientAiException | NonTransientAiException | ResourceAccessException exception) {
            throw new AiModelUnavailableException(exception);
        }

        if (!StringUtils.hasText(answer)) {
            throw new AiModelEmptyResponseException();
        }

        return new AiAgentResponse(answer, AiAgentResponse.Status.COMPLETED, conversation.id());
    }

    private ToolCallback observeToolCallback(String conversationId, ToolCallback callback) {
        return new ObservedToolCallback(conversationId, callback, toolInvocationRecorder);
    }
}
