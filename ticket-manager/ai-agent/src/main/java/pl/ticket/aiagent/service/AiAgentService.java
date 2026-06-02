package pl.ticket.aiagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import pl.ticket.aiagent.dto.AiAgentResponse;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;
import pl.ticket.aiagent.exception.AiModelEmptyResponseException;
import pl.ticket.aiagent.exception.AiModelUnavailableException;
import pl.ticket.aiagent.model.AgentRun;
import pl.ticket.aiagent.tools.SelectedToolCallbackResolver;
import pl.ticket.aiagent.tools.ToolCandidateSelector;

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
        AgentRun run = AgentRun.started(userMessage, callerContext);
        run = run.withSelectedTools(toolCandidateSelector.selectFor(run.userMessage(), run.callerContext()));
        run = run.withResolvedCallbacks(toolCallbackResolver.resolve(run.selectedTools()));

        String answer;
        try {
            answer = chatClient.prompt()
                    .user(run.userMessage())
                    .toolCallbacks(run.toolCallbackResolution().callbacks())
                    .call()
                    .content();
        } catch (TransientAiException | NonTransientAiException | ResourceAccessException exception) {
            throw new AiModelUnavailableException(exception);
        }

        run = run.completed(answer);

        if (!StringUtils.hasText(run.answer())) {
            throw new AiModelEmptyResponseException();
        }

        return new AiAgentResponse(run.answer(), AiAgentResponse.Status.COMPLETED);
    }
}
