package pl.ticket.event.internal.ticket.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.ticket.event.admin.event.exception.InvalidRequestedDataException;

@ControllerAdvice
public class InternalTicketExceptionHandler
{
    @ExceptionHandler(value = {ReservationProcessException.class})
    public void handleReservationProcessException(ReservationProcessException ex)
    {
        int i =0;
        //publish reservation reject to queue
    }
}
