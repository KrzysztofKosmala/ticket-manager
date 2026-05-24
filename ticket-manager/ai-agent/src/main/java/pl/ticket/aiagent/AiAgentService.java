package pl.ticket.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.api.AiAgentResponse;
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

    public AiAgentService(
            ChatClient.Builder chatClientBuilder,
            AiAgentInstructions instructions,
            ToolCandidateSelector toolCandidateSelector,
            SelectedToolCallbackResolver toolCallbackResolver
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem(instructions.systemPrompt())
                .build();
        this.toolCandidateSelector = toolCandidateSelector;
        this.toolCallbackResolver = toolCallbackResolver;
    }

    public AiAgentResponse ask(String userMessage) {
        List<ToolCandidate> candidates = toolCandidateSelector.selectFor(userMessage);
        ToolCallbackResolution callbackResolution = toolCallbackResolver.resolve(candidates);
        if (!callbackResolution.missingCandidates().isEmpty()) {
            return fallback();
        }

        String answer;
        try {
            ChatClient.ChatClientRequestSpec request = chatClient.prompt()
                    .user(userMessage);

            if (callbackResolution.hasCallbacks()) {
                request = request.toolCallbacks(callbackResolution.callbacks());
            }

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
