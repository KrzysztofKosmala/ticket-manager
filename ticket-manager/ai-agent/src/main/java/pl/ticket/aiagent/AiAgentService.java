package pl.ticket.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.ticket.aiagent.api.AiAgentResponse;
import pl.ticket.aiagent.toolselection.ToolCandidateSelector;

@Service
public class AiAgentService
{
    private static final String FALLBACK_ANSWER =
            "Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.";

    private final ChatClient chatClient;
    private final ToolCandidateSelector toolCandidateSelector;

    public AiAgentService(
            ChatClient.Builder chatClientBuilder,
            AiAgentInstructions instructions,
            ToolCandidateSelector toolCandidateSelector
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem(instructions.systemPrompt())
                .build();
        this.toolCandidateSelector = toolCandidateSelector;
    }

    public AiAgentResponse ask(String userMessage) {
        toolCandidateSelector.selectFor(userMessage);

        String answer;
        try {
            answer = chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();
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
