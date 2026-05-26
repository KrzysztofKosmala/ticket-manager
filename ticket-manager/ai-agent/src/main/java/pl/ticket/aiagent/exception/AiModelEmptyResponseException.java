package pl.ticket.aiagent.exception;

public class AiModelEmptyResponseException extends RuntimeException {

    public AiModelEmptyResponseException() {
        super("AI model returned an empty response");
    }
}
