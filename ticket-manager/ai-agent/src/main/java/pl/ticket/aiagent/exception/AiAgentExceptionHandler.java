package pl.ticket.aiagent.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.ticket.aiagent.dto.AiAgentResponse;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class AiAgentExceptionHandler {

    private static final String FALLBACK_ANSWER =
            "Nie udalo mi sie teraz przygotowac odpowiedzi. Sprobuj ponownie za chwile.";

    @ExceptionHandler({
            AiModelUnavailableException.class,
            AiModelEmptyResponseException.class
    })
    public ResponseEntity<AiAgentResponse> handleAiModelFallback(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(new AiAgentResponse(FALLBACK_ANSWER, AiAgentResponse.Status.FALLBACK));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ApiError.FieldViolation> fieldViolations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiError.FieldViolation(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .sorted(Comparator.comparing(ApiError.FieldViolation::field))
                .toList();

        ApiError apiError = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Request validation failed",
                request.getRequestURI(),
                fieldViolations
        );

        return ResponseEntity.status(status).body(apiError);
    }
}
