package pl.ticket.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.api.AiAgentResponse;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.caller.CallerContextProvider;
import pl.ticket.aiagent.toolcallback.SelectedToolCallbackResolver;
import pl.ticket.aiagent.toolcallback.ToolCallbackResolution;
import pl.ticket.aiagent.toolselection.ToolCandidate;
import pl.ticket.aiagent.toolselection.ToolCandidateSelector;

import java.util.List;

@Service
public class AiAgentService
{
    private static final String FALLBACK_ANSWER =
            "Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.";

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
            ChatClient.ChatClientRequestSpec request = chatClient.prompt()
                    .user(userMessage);

            request = request.toolCallbacks(callbackResolution.callbacks());

            answer = request.call().content();
        } catch (RuntimeException exception) {
            return fallback();
        }

        if (!StringUtils.hasText(answer)) {
            return fallback();
        }

        return new AiAgentResponse(answer, AiAgentResponse.Status.COMPLETED);
    }

    private AiAgentResponse fallback() {
        return new AiAgentResponse(FALLBACK_ANSWER, AiAgentResponse.Status.FALLBACK);
    }
}
