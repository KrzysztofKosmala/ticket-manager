package pl.ticket.aiagent.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import pl.ticket.aiagent.conversation.Conversation;
import pl.ticket.aiagent.conversation.ConversationMessage;
import pl.ticket.aiagent.conversation.ConversationStore;
import pl.ticket.aiagent.dto.AiAgentResponse;
import pl.ticket.aiagent.exception.AiModelEmptyResponseException;
import pl.ticket.aiagent.exception.AiModelUnavailableException;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.security.CallerContextProvider;
import pl.ticket.aiagent.tools.ObservedToolCallback;
import pl.ticket.aiagent.tools.SelectedToolCallbackResolver;
import pl.ticket.aiagent.tools.ToolCandidate;
import pl.ticket.aiagent.tools.ToolCandidateSelector;
import pl.ticket.aiagent.tools.ToolInvocationRecorder;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class AiAgentServiceTest {

    private static final String MESSAGE = "Pokaz moje zamowienia";

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
    void shouldRegisterChatMemoryAdvisorOnChatClient() {
        Fixture fixture = new Fixture();

        fixture.service();

        verify(fixture.builder).defaultAdvisors(fixture.chatMemoryAdvisor);
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
    void shouldCreateConversationForChatMemoryAdvisorWithoutStoringMessagesDirectly() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.newConversation();
        fixture.modelAnswers("Masz 2 zamowienia.");

        AiAgentResponse response = fixture.service().ask(MESSAGE);

        assertThat(response.conversationId()).isEqualTo("conversation-123");
        verify(fixture.conversationStore).getOrCreate(null, fixture.callerContext);
        verify(fixture.conversationStore, never()).appendMessage(
                anyString(),
                any(CallerContext.class),
                any(ConversationMessage.class)
        );
    }

    @Test
    void shouldPassConversationIdToChatMemoryAdvisor() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.newConversation();
        fixture.modelAnswers("Masz 2 zamowienia.");

        fixture.service().ask(MESSAGE);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<ChatClient.AdvisorSpec>> advisorSpecCaptor =
                ArgumentCaptor.forClass(Consumer.class);
        verify(fixture.requestSpec).advisors(advisorSpecCaptor.capture());

        ChatClient.AdvisorSpec advisorSpec = mock(ChatClient.AdvisorSpec.class);
        advisorSpecCaptor.getValue().accept(advisorSpec);

        verify(advisorSpec).param(ChatMemory.CONVERSATION_ID, "conversation-123");
    }

    @Test
    void shouldPassObservedResolvedToolCallbacksToChatClientRequest() {
        Fixture fixture = new Fixture();
        ToolCandidate candidate = new ToolCandidate("tm_orders_search");
        ToolCallback toolCallback = mock(ToolCallback.class);
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        List<ToolCandidate> candidates = List.of(candidate);
        List<ToolCallback> callbacks = List.of(toolCallback);
        fixture.selectedTools(candidates, callbacks);
        fixture.newConversation();
        fixture.modelAnswers("Masz 2 zamowienia.");
        when(toolCallback.getToolDefinition()).thenReturn(toolDefinition);
        when(toolDefinition.name()).thenReturn("tm_orders_search");
        when(toolCallback.call("{}")).thenReturn("{\"orders\":[]}");

        AiAgentResponse response = fixture.service().ask(MESSAGE);

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ToolCallback>> callbacksCaptor = ArgumentCaptor.forClass(List.class);
        verify(fixture.requestSpec).toolCallbacks(callbacksCaptor.capture());

        List<ToolCallback> observedCallbacks = callbacksCaptor.getValue();
        assertThat(observedCallbacks)
                .hasSize(1)
                .first()
                .isInstanceOf(ObservedToolCallback.class);

        String toolResult = observedCallbacks.get(0).call("{}");

        assertThat(toolResult).isEqualTo("{\"orders\":[]}");
        verify(fixture.toolInvocationRecorder).recordSuccess(
                "conversation-123",
                "tm_orders_search",
                "{}",
                "{\"orders\":[]}"
        );
    }

    @Test
    void shouldLogToolSelectionAndResolution(CapturedOutput output) {
        Fixture fixture = new Fixture();
        ToolCandidate candidate = new ToolCandidate("tm_orders_search");
        ToolCallback toolCallback = mock(ToolCallback.class);
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        List<ToolCandidate> candidates = List.of(candidate);
        fixture.selectedTools(candidates, List.of(toolCallback));
        fixture.newConversation();
        fixture.modelAnswers("Masz 2 zamowienia.");
        when(toolCallback.getToolDefinition()).thenReturn(toolDefinition);
        when(toolDefinition.name()).thenReturn("tm_orders_search");

        fixture.service().ask(MESSAGE);

        assertThat(output)
                .contains("AI agent request started: conversationId=conversation-123, selectedTools=[tm_orders_search]")
                .contains("AI agent resolved tool callbacks: conversationId=conversation-123, callbacks=[tm_orders_search]")
                .contains("AI agent request completed: conversationId=conversation-123");
    }

    @Test
    void shouldAttachEmptyToolCallbacksWhenNoToolsWereSelected() {
        Fixture fixture = new Fixture();
        fixture.noSelectedTools();
        fixture.newConversation();
        fixture.modelAnswers("Masz 2 zamowienia.");

        fixture.service().ask(MESSAGE);

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
        when(fixture.requestSpec.advisors(org.mockito.ArgumentMatchers.<Consumer<ChatClient.AdvisorSpec>>any()))
                .thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.toolCallbacks(List.of())).thenReturn(fixture.requestSpec);
        when(fixture.requestSpec.call()).thenThrow(new TransientAiException("provider unavailable"));

        assertThatThrownBy(() -> fixture.service().ask(MESSAGE))
                .isInstanceOf(AiModelUnavailableException.class)
                .hasCauseInstanceOf(TransientAiException.class);
    }

    private static class Fixture {

        private final AiAgentInstructions instructions = new AiAgentInstructions();
        private final MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(
                MessageWindowChatMemory.builder().build()
        ).build();
        private final ChatClient.Builder builder = mock(ChatClient.Builder.class);
        private final ChatClient chatClient = mock(ChatClient.class);
        private final ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        private final ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        private final ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        private final SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);
        private final ToolInvocationRecorder toolInvocationRecorder = mock(ToolInvocationRecorder.class);
        private final CallerContextProvider callerContextProvider = mock(CallerContextProvider.class);
        private final ConversationStore conversationStore = mock(ConversationStore.class);
        private final AiAgentFlowLogger flowLogger = new AiAgentFlowLogger();
        private final CallerContext callerContext = CallerContext.anonymous();
        private final Conversation conversation = new Conversation(
                "conversation-123",
                callerContext.subject(),
                List.of()
        );

        private Fixture() {
            when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
            when(builder.defaultAdvisors(chatMemoryAdvisor)).thenReturn(builder);
            when(builder.build()).thenReturn(chatClient);
            when(callerContextProvider.current()).thenReturn(callerContext);
        }

        private AiAgentService service() {
            return new AiAgentService(
                    builder,
                    instructions,
                    chatMemoryAdvisor,
                    toolCandidateSelector,
                    toolCallbackResolver,
                    toolInvocationRecorder,
                    callerContextProvider,
                    conversationStore,
                    flowLogger
            );
        }

        private void noSelectedTools() {
            selectedTools(List.of(), List.of());
        }

        private void selectedTools(List<ToolCandidate> candidates, List<ToolCallback> callbacks) {
            when(toolCandidateSelector.selectFor(MESSAGE, callerContext)).thenReturn(candidates);
            when(toolCallbackResolver.resolve(candidates)).thenReturn(callbacks);
        }

        private void newConversation() {
            when(conversationStore.getOrCreate(null, callerContext)).thenReturn(conversation);
        }

        private void modelAnswers(String answer) {
            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(MESSAGE)).thenReturn(requestSpec);
            when(requestSpec.advisors(org.mockito.ArgumentMatchers.<Consumer<ChatClient.AdvisorSpec>>any()))
                    .thenReturn(requestSpec);
            when(requestSpec.toolCallbacks(org.mockito.ArgumentMatchers.<List<ToolCallback>>any())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callResponseSpec);
            when(callResponseSpec.content()).thenReturn(answer);
        }
    }
}
