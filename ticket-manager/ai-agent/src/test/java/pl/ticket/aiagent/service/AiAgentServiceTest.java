package pl.ticket.aiagent.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.tool.ToolCallback;
import pl.ticket.aiagent.conversation.Conversation;
import pl.ticket.aiagent.conversation.ConversationMessage;
import pl.ticket.aiagent.conversation.ConversationStore;
import pl.ticket.aiagent.dto.AiAgentResponse;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;
import pl.ticket.aiagent.exception.AiModelEmptyResponseException;
import pl.ticket.aiagent.exception.AiModelUnavailableException;
import pl.ticket.aiagent.tools.SelectedToolCallbackResolver;
import pl.ticket.aiagent.tools.ToolCallbackResolution;
import pl.ticket.aiagent.tools.ToolCandidate;
import pl.ticket.aiagent.tools.ToolCandidateSelector;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        fixture.newConversation();
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
        fixture.newConversation();
        fixture.modelAnswers("Masz 2 zamowienia.");

        fixture.service().ask(MESSAGE);

        verify(fixture.callerContextProvider).current();
        verify(fixture.toolCandidateSelector).selectFor(MESSAGE, fixture.callerContext);
    }

    @Test
    void shouldCreateConversationAndStoreUserAndAssistantMessages() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.newConversation();
        fixture.modelAnswers("Masz 2 zamowienia.");

        AiAgentResponse response = fixture.service().ask(MESSAGE);

        assertThat(response.conversationId()).isEqualTo("conversation-123");
        verify(fixture.conversationStore).getOrCreate(null, fixture.callerContext);
        verify(fixture.conversationStore).appendMessage(
                "conversation-123",
                fixture.callerContext,
                ConversationMessage.user(MESSAGE)
        );
        verify(fixture.conversationStore).appendMessage(
                "conversation-123",
                fixture.callerContext,
                ConversationMessage.assistant("Masz 2 zamowienia.")
        );
    }

    @Test
    void shouldPassResolvedToolCallbacksToChatClientRequest() {
        Fixture fixture = new Fixture();
        ToolCandidate candidate = new ToolCandidate("tm.orders.search");
        ToolCallback toolCallback = mock(ToolCallback.class);
        List<ToolCandidate> candidates = List.of(candidate);
        List<ToolCallback> callbacks = List.of(toolCallback);
        fixture.selectedTools(candidates, ToolCallbackResolution.of(callbacks));
        fixture.newConversation();
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
        fixture.newConversation();
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
        fixture.newConversation();
        fixture.modelAnswers("Nie mam teraz dostepu do danych zamowien, ale moge pomoc ogolnie.");

        AiAgentResponse response = fixture.service().ask(MESSAGE);

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        assertThat(response.answer()).isEqualTo("Nie mam teraz dostepu do danych zamowien, ale moge pomoc ogolnie.");
        verify(fixture.requestSpec).toolCallbacks(List.of());
    }

    @Test
    void shouldThrowWhenModelAnswerIsBlank() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.newConversation();
        fixture.modelAnswers("   ");

        assertThatThrownBy(() -> fixture.service().ask(MESSAGE))
                .isInstanceOf(AiModelEmptyResponseException.class);
    }

    @Test
    void shouldThrowAiModelUnavailableWhenChatClientFailsWithKnownAiException() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.newConversation();
        when(fixture.chatClient.prompt()).thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.user(MESSAGE)).thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.toolCallbacks(List.of())).thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.call()).thenThrow(new TransientAiException("provider unavailable"));

        assertThatThrownBy(() -> fixture.service().ask(MESSAGE))
                .isInstanceOf(AiModelUnavailableException.class)
                .hasCauseInstanceOf(TransientAiException.class);
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
        private final ConversationStore conversationStore = mock(ConversationStore.class);
        private final CallerContext callerContext = CallerContext.anonymous();
        private final Conversation conversation = new Conversation(
                "conversation-123",
                callerContext.subject(),
                List.of()
        );

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
                    conversationStore
            );
        }

        private void noSelectedTools() {
            selectedTools(List.of(), ToolCallbackResolution.empty());
        }

        private void selectedTools(List<ToolCandidate> candidates, ToolCallbackResolution resolution) {
            when(toolCandidateSelector.selectFor(MESSAGE, callerContext)).thenReturn(candidates);
            when(toolCallbackResolver.resolve(candidates)).thenReturn(resolution);
        }

        private void newConversation() {
            when(conversationStore.getOrCreate(null, callerContext)).thenReturn(conversation);
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
