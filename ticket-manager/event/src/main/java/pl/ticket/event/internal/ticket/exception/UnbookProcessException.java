package pl.ticket.event.internal.ticket.exception;

import lombok.Getter;
import pl.ticket.dto.OrderEvent;
@Getter
public class UnbookProcessException extends RuntimeException
{
    private final OrderEvent order;
    public UnbookProcessException(String message, OrderEvent order)
    {
        super(message);
        this.order = order;
    }
}
