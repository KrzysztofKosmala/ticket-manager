package pl.ticket.event.customer.event.exception;

public class EventDataException extends RuntimeException {
    public EventDataException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EventDataException(String message)
    {
        super(message);
    }
}
