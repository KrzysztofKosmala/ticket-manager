package pl.ticket.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import pl.ticket.aiagent.api.AiAgentResponse;

@Service
public class AiAgentService
{
    private final ChatClient chatClient;

    public AiAgentService(ChatClient.Builder chatClientBuilder, AiAgentInstructions instructions) {
        this.chatClient = chatClientBuilder
                .defaultSystem(instructions.systemPrompt())
                .build();
    }

    public AiAgentResponse ask(String userMessage) {
        String answer = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
        return new AiAgentResponse(answer, AiAgentResponse.Status.COMPLETED);
    }
}
