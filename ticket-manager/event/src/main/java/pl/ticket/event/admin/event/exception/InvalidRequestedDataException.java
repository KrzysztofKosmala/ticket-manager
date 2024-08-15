package pl.ticket.event.admin.event.exception;

public class InvalidRequestedDataException extends RuntimeException
{
    public InvalidRequestedDataException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidRequestedDataException(String message)
    {
        super(message);
    }
}
