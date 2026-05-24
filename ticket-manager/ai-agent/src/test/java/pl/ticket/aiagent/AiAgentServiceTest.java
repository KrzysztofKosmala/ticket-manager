package pl.ticket.aiagent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import pl.ticket.aiagent.api.AiAgentResponse;
import pl.ticket.aiagent.toolselection.ToolCandidateSelector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Masz 2 zamowienia.");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector);

        AiAgentResponse response = service.ask("Pokaz moje zamowienia");

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.COMPLETED);
        assertThat(response.answer()).isEqualTo("Masz 2 zamowienia.");
        verify(builder).defaultSystem(instructions.systemPrompt());
        verify(toolCandidateSelector).selectFor("Pokaz moje zamowienia");
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

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("Masz 2 zamowienia.");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector);

        service.ask("Pokaz moje zamowienia");

        verify(toolCandidateSelector).selectFor("Pokaz moje zamowienia");
    }

    @Test
    void shouldReturnFallbackWhenModelAnswerIsBlank() {
        AiAgentInstructions instructions = new AiAgentInstructions();
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = mock(ChatClient.CallResponseSpec.class);
        ToolCandidateSelector toolCandidateSelector = mock(ToolCandidateSelector.class);

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("   ");

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector);

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

        when(builder.defaultSystem(instructions.systemPrompt())).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Pokaz moje zamowienia")).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new IllegalStateException("provider unavailable"));

        AiAgentService service = new AiAgentService(builder, instructions, toolCandidateSelector);

        AiAgentResponse response = service.ask("Pokaz moje zamowienia");

        assertThat(response.status()).isEqualTo(AiAgentResponse.Status.FALLBACK);
        assertThat(response.answer()).isEqualTo("Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.");
    }
}
