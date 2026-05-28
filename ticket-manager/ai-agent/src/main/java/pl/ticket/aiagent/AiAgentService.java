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
import pl.ticket.aiagent.run.AgentRun;
import pl.ticket.aiagent.run.AgentRunLogger;
import pl.ticket.aiagent.toolcallback.SelectedToolCallbackResolver;
import pl.ticket.aiagent.toolselection.ToolCandidateSelector;

@Service
public class AiAgentService
{
    private final ChatClient chatClient;
    private final ToolCandidateSelector toolCandidateSelector;
    private final SelectedToolCallbackResolver toolCallbackResolver;
    private final CallerContextProvider callerContextProvider;
    private final AgentRunLogger agentRunLogger;

    public AiAgentService(
            ChatClient.Builder chatClientBuilder,
            AiAgentInstructions instructions,
            ToolCandidateSelector toolCandidateSelector,
            SelectedToolCallbackResolver toolCallbackResolver,
            CallerContextProvider callerContextProvider,
            AgentRunLogger agentRunLogger
    ) {
        this.chatClient = chatClientBuilder
                .defaultSystem(instructions.systemPrompt())
                .build();
        this.toolCandidateSelector = toolCandidateSelector;
        this.toolCallbackResolver = toolCallbackResolver;
        this.callerContextProvider = callerContextProvider;
        this.agentRunLogger = agentRunLogger;
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

        agentRunLogger.logCompleted(run);
        return new AiAgentResponse(run.answer(), AiAgentResponse.Status.COMPLETED);
    }
}
