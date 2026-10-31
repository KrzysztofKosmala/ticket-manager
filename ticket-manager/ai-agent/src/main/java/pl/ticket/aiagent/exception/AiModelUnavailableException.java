package pl.ticket.aiagent.exception;

public class AiModelUnavailableException extends RuntimeException {

    public AiModelUnavailableException(Throwable cause) {
        super("AI model is unavailable", cause);
    }
}
