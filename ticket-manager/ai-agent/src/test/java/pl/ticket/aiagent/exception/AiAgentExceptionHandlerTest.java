package pl.ticket.aiagent.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import pl.ticket.aiagent.api.AiAgentResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AiAgentExceptionHandlerTest {

    @Test
    void shouldReturnFallbackResponseWhenAiModelIsUnavailable() {
        AiAgentExceptionHandler handler = new AiAgentExceptionHandler();

        ResponseEntity<AiAgentResponse> response = handler.handleAiModelFallback(
                new AiModelUnavailableException(new RuntimeException("provider unavailable")),
                mock(HttpServletRequest.class)
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody())
                .isEqualTo(new AiAgentResponse(
                        "Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.",
                        AiAgentResponse.Status.FALLBACK
                ));
    }

    @Test
    void shouldReturnFallbackResponseWhenAiModelReturnsEmptyAnswer() {
        AiAgentExceptionHandler handler = new AiAgentExceptionHandler();

        ResponseEntity<AiAgentResponse> response = handler.handleAiModelFallback(
                new AiModelEmptyResponseException(),
                mock(HttpServletRequest.class)
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody())
                .extracting(AiAgentResponse::status)
                .isEqualTo(AiAgentResponse.Status.FALLBACK);
    }
}
