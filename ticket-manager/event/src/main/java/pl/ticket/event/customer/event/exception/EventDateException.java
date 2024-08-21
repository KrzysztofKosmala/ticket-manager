package pl.ticket.event.customer.event.exception;

public class EventDateException extends RuntimeException {
    public EventDateException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EventDateException(String message)
    {
        super(message);
    }
}
