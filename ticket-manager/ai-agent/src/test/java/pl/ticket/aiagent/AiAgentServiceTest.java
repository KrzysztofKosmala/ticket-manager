package pl.ticket.aiagent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.tool.ToolCallback;
import pl.ticket.aiagent.api.AiAgentResponse;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.caller.CallerContextProvider;
import pl.ticket.aiagent.exception.AiModelEmptyResponseException;
import pl.ticket.aiagent.exception.AiModelUnavailableException;
import pl.ticket.aiagent.run.AgentRun;
import pl.ticket.aiagent.run.AgentRunLogger;
import pl.ticket.aiagent.toolcallback.SelectedToolCallbackResolver;
import pl.ticket.aiagent.toolcallback.ToolCallbackResolution;
import pl.ticket.aiagent.toolselection.ToolCandidate;
import pl.ticket.aiagent.toolselection.ToolCandidateSelector;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiAgentServiceTest {

    private static final String MESSAGE = "Pokaz moje zamowienia";
    private static final String FALLBACK_ANSWER =
            "Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.";

    @Test
    void shouldAskModelWithSystemInstructions() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.modelAnswers("Masz 2 zamowienia.");

        AiAgentResponse response = fixture.service().ask(MESSAGE);

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        assertThat(response.answer()).isEqualTo("Masz 2 zamowienia.");
        verify(fixture.builder).defaultSystem(fixture.instructions.systemPrompt());
        verify(fixture.requestSpec).user(MESSAGE);
    }

    @Test
    void shouldSelectToolCandidatesForCurrentCallerBeforeAskingModel() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.modelAnswers("Masz 2 zamowienia.");

        fixture.service().ask(MESSAGE);

        verify(fixture.callerContextProvider).current();
        verify(fixture.toolCandidateSelector).selectFor(MESSAGE, fixture.callerContext);
    }

    @Test
    void shouldPassResolvedToolCallbacksToChatClientRequest() {
        Fixture fixture = new Fixture();
        ToolCandidate candidate = new ToolCandidate("tm.orders.search");
        ToolCallback toolCallback = mock(ToolCallback.class);
        List<ToolCandidate> candidates = List.of(candidate);
        List<ToolCallback> callbacks = List.of(toolCallback);
        fixture.selectedTools(candidates, ToolCallbackResolution.of(callbacks));
        fixture.modelAnswers("Masz 2 zamowienia.");
        when(fixture.requestSpec.toolCallbacks(callbacks)).thenReturn(fixture.requestSpec);

        AiAgentResponse response = fixture.service().ask(MESSAGE);

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        verify(fixture.requestSpec).toolCallbacks(callbacks);
    }

    @Test
    void shouldAttachEmptyToolCallbacksWhenResolverReturnsNone() {
        Fixture fixture = new Fixture();
        ToolCandidate candidate = new ToolCandidate("tm.orders.search");
        List<ToolCandidate> candidates = List.of(candidate);
        fixture.selectedTools(candidates, ToolCallbackResolution.empty());
        fixture.modelAnswers("Masz 2 zamowienia.");

        fixture.service().ask(MESSAGE);

        verify(fixture.requestSpec).toolCallbacks(List.of());
    }

    @Test
    void shouldAskModelWithoutToolCallbacksWhenSelectedToolCallbackIsMissing() {
        Fixture fixture = new Fixture();
        ToolCandidate candidate = new ToolCandidate("tm.orders.search");
        List<ToolCandidate> candidates = List.of(candidate);
        fixture.selectedTools(candidates, new ToolCallbackResolution(List.of(), candidates));
        fixture.modelAnswers("Nie mam teraz dostepu do danych zamowien, ale moge pomoc ogolnie.");

        AiAgentResponse response = fixture.service().ask(MESSAGE);

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        assertThat(response.answer()).isEqualTo("Nie mam teraz dostepu do danych zamowien, ale moge pomoc ogolnie.");
        verify(fixture.requestSpec).toolCallbacks(List.of());
    }

    @Test
    void shouldLogCompletedRunAfterSuccessfulModelAnswer() {
        Fixture fixture = new Fixture();
        ToolCandidate candidate = new ToolCandidate("tm.orders.search");
        List<ToolCandidate> candidates = List.of(candidate);
        ToolCallbackResolution resolution = new ToolCallbackResolution(List.of(), candidates);
        fixture.selectedTools(candidates, resolution);
        fixture.modelAnswers("Nie mam teraz dostepu do danych zamowien, ale moge pomoc ogolnie.");

        fixture.service().ask(MESSAGE);

        org.mockito.ArgumentCaptor<AgentRun> runCaptor = org.mockito.ArgumentCaptor.forClass(AgentRun.class);
        verify(fixture.agentRunLogger).logCompleted(runCaptor.capture());
        AgentRun loggedRun = runCaptor.getValue();
        assertThat(loggedRun.userMessage()).isEqualTo(MESSAGE);
        assertThat(loggedRun.callerContext()).isEqualTo(fixture.callerContext);
        assertThat(loggedRun.selectedTools()).containsExactly(candidate);
        assertThat(loggedRun.toolCallbackResolution()).isEqualTo(resolution);
        assertThat(loggedRun.answer())
                .isEqualTo("Nie mam teraz dostepu do danych zamowien, ale moge pomoc ogolnie.");
    }

    @Test
    void shouldThrowWhenModelAnswerIsBlank() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.modelAnswers("   ");

        assertThatThrownBy(() -> fixture.service().ask(MESSAGE))
                .isInstanceOf(AiModelEmptyResponseException.class);
        verify(fixture.agentRunLogger, never()).logCompleted(any());
    }

    @Test
    void shouldThrowAiModelUnavailableWhenChatClientFailsWithKnownAiException() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        when(fixture.chatClient.prompt()).thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.user(MESSAGE)).thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.toolCallbacks(List.of())).thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.call()).thenThrow(new TransientAiException("provider unavailable"));

        assertThatThrownBy(() -> fixture.service().ask(MESSAGE))
                .isInstanceOf(AiModelUnavailableException.class)
                .hasCauseInstanceOf(TransientAiException.class);
        verify(fixture.agentRunLogger, never()).logCompleted(any());
    }

    private static class Fixture {

        private final AiAgentInstructions instructions = new AiAgentInstructions();
        private final ChatClient.Builder builder = mock(ChatClient.Builder.class);
        private final ChatClient chatClient = mock(ChatClient.class);
        private final ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        private final ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        private final ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        private final SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);
        private final CallerContextProvider callerContextProvider = mock(CallerContextProvider.class);
        private final AgentRunLogger agentRunLogger = mock(AgentRunLogger.class);
        private final CallerContext callerContext = CallerContext.anonymous();

        private Fixture() {
            when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
            when(builder.build()).thenReturn(chatClient);
            when(callerContextProvider.current()).thenReturn(callerContext);
        }

        private AiAgentService service() {
            return new AiAgentService(
                    builder,
                    instructions,
                    toolCandidateSelector,
                    toolCallbackResolver,
                    callerContextProvider,
                    agentRunLogger
            );
        }

        private void noSelectedTools() {
            selectedTools(List.of(), ToolCallbackResolution.empty());
        }

        private void selectedTools(List<ToolCandidate> candidates, ToolCallbackResolution resolution) {
            when(toolCandidateSelector.selectFor(MESSAGE, callerContext)).thenReturn(candidates);
            when(toolCallbackResolver.resolve(candidates)).thenReturn(resolution);
        }

        private void modelAnswers(String answer) {
            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(MESSAGE)).thenReturn(requestSpec);
            when(requestSpec.toolCallbacks(List.of())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.content()).thenReturn(answer);
        }
    }
}
