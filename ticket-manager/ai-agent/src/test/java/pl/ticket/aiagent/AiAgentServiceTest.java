package pl.ticket.aiagent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import pl.ticket.aiagent.api.AiAgentResponse;
import pl.ticket.aiagent.toolcallback.SelectedToolCallbackResolver;
import pl.ticket.aiagent.toolcallback.ToolCallbackResolution;
import pl.ticket.aiagent.toolselection.ToolCandidate;
import pl.ticket.aiagent.toolselection.ToolCandidateSelector;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiAgentServiceTest {

    @Test
    void shouldAskModelWithSystemInstructions() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(toolCandidateSelector.selectFor("Pokaz moje zamowienia")).thenReturn(List.of());
        when(toolCallbackResolver.resolve(List.of())).thenReturn(ToolCallbackResolution.empty());
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Masz 2 zamowienia.");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector, toolCallbackResolver);

        AiAgentResponse response = service.ask("Pokaz moje zamowienia");

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        assertThat(response.answer()).isEqualTo("Masz 2 zamowienia.");
        verify(builder).defaultSystem(instructions.systemPrompt());
        verify(toolCandidateSelector).selectFor("Pokaz moje zamowienia");
        verify(toolCallbackResolver).resolve(List.of());
        verify(requestSpec).user("Pokaz moje zamowienia");
    }

    @Test
    void shouldSelectToolCandidatesBeforeAskingModel() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(toolCandidateSelector.selectFor("Pokaz moje zamowienia")).thenReturn(List.of());
        when(toolCallbackResolver.resolve(List.of())).thenReturn(ToolCallbackResolution.empty());
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Masz 2 zamowienia.");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector, toolCallbackResolver);

        service.ask("Pokaz moje zamowienia");

        verify(toolCandidateSelector).selectFor("Pokaz moje zamowienia");
    }

    @Test
    void shouldPassResolvedToolCallbacksToChatClientRequest() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);
        ToolCandidate candidate = new ToolCandidate("tm.orders.search", "Search orders");
        ToolCallback toolCallback = mock(ToolCallback.class);
        List<ToolCandidate> candidates = List.of(candidate);
        List<ToolCallback> callbacks = List.of(toolCallback);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(toolCandidateSelector.selectFor("Pokaz moje zamowienia")).thenReturn(candidates);
        when(toolCallbackResolver.resolve(candidates)).thenReturn(ToolCallbackResolution.of(callbacks));
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.toolCallbacks(callbacks)).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Masz 2 zamowienia.");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector, toolCallbackResolver);

        AiAgentResponse response = service.ask("Pokaz moje zamowienia");

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        verify(requestSpec).toolCallbacks(callbacks);
    }

    @Test
    void shouldNotAttachToolCallbacksWhenResolverReturnsNone() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);
        ToolCandidate candidate = new ToolCandidate("tm.orders.search", "Search orders");
        List<ToolCandidate> candidates = List.of(candidate);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(toolCandidateSelector.selectFor("Pokaz moje zamowienia")).thenReturn(candidates);
        when(toolCallbackResolver.resolve(candidates)).thenReturn(ToolCallbackResolution.empty());
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Masz 2 zamowienia.");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector, toolCallbackResolver);

        service.ask("Pokaz moje zamowienia");

        verify(requestSpec, never()).toolCallbacks(anyList());
    }

    @Test
    void shouldReturnFallbackWhenSelectedToolCallbackIsMissing() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);
        ToolCandidate candidate = new ToolCandidate("tm.orders.search", "Search orders");
        List<ToolCandidate> candidates = List.of(candidate);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(toolCandidateSelector.selectFor("Pokaz moje zamowienia")).thenReturn(candidates);
        when(toolCallbackResolver.resolve(candidates)).thenReturn(new ToolCallbackResolution(List.of(), candidates));

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector, toolCallbackResolver);

        AiAgentResponse response = service.ask("Pokaz moje zamowienia");

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.FALLBACK);
        assertThat(response.answer()).isEqualTo("Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.");
        verify(chatClient, never()).prompt();
    }

    @Test
    void shouldReturnFallbackWhenModelAnswerIsBlank() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(toolCandidateSelector.selectFor("Pokaz moje zamowienia")).thenReturn(List.of());
        when(toolCallbackResolver.resolve(List.of())).thenReturn(ToolCallbackResolution.empty());
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("   ");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector, toolCallbackResolver);

        AiAgentResponse response = service.ask("Pokaz moje zamowienia");

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.FALLBACK);
        assertThat(response.answer()).isEqualTo("Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.");
    }

    @Test
    void shouldReturnFallbackWhenChatClientFails() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);
        SelectedToolCallbackResolver toolCallbackResolver = mock(SelectedToolCallbackResolver.class);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(toolCandidateSelector.selectFor("Pokaz moje zamowienia")).thenReturn(List.of());
        when(toolCallbackResolver.resolve(List.of())).thenReturn(ToolCallbackResolution.empty());
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new IllegalStateException("provider unavailable"));

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector, toolCallbackResolver);

        AiAgentResponse response = service.ask("Pokaz moje zamowienia");

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.FALLBACK);
        assertThat(response.answer()).isEqualTo("Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.");
    }
}
