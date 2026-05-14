package pl.ticket.aiagent.exception;

public class PlannerFailedException extends RuntimeException {

    public PlannerFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
