package pl.ticket.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import pl.ticket.aiagent.api.AiAgentResponse;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.caller.CallerContextProvider;
import pl.ticket.aiagent.exception.AiModelEmptyResponseException;
import pl.ticket.aiagent.exception.AiModelUnavailableException;
import pl.ticket.aiagent.toolcallback.SelectedToolCallbackResolver;
import pl.ticket.aiagent.toolcallback.ToolCallbackResolution;
import pl.ticket.aiagent.toolselection.ToolCandidate;
import pl.ticket.aiagent.toolselection.ToolCandidateSelector;

import java.util.List;

@Service
public class AiAgentService
{
    private final ChatClient chatClient;
    private final ToolCandidateSelector toolCandidateSelector;
    private final SelectedToolCallbackResolver toolCallbackResolver;
    private final CallerContextProvider callerContextProvider;

    public AiAgentService(
            ChatClient.Builder chatClientBuilder,
            AiAgentInstructions instructions,
            ToolCandidateSelector toolCandidateSelector,
            SelectedToolCallbackResolver toolCallbackResolver,
            CallerContextProvider callerContextProvider
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem(instructions.systemPrompt())
                .build();
        this.toolCandidateSelector = toolCandidateSelector;
        this.toolCallbackResolver = toolCallbackResolver;
        this.callerContextProvider = callerContextProvider;
    }

    public AiAgentResponse ask(String userMessage) {
        CallerContext callerContext = callerContextProvider.current();
        List<ToolCandidate> candidates = toolCandidateSelector.selectFor(userMessage, callerContext);
        ToolCallbackResolution callbackResolution = toolCallbackResolver.resolve(candidates);

        String answer;
        try {
            answer = chatClient.prompt()
                    .user(userMessage)
                    .toolCallbacks(callbackResolution.callbacks())
                    .call()
                    .content();
        } catch (TransientAiException | NonTransientAiException | ResourceAccessException exception) {
            throw new AiModelUnavailableException(exception);
        }

        if (!StringUtils.hasText(answer)) {
            throw new AiModelEmptyResponseException();
        }

        return new AiAgentResponse(answer, AiAgentResponse.Status.COMPLETED);
    }
}
