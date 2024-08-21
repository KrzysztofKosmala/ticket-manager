package pl.ticket.event.customer.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class EventExceptionHandler {
    @ExceptionHandler(value = {EventDateException.class})
    public ResponseEntity<Object> handleEvenDateException(EventDateException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        EventException eventException = new EventException
                (
                        ex.getMessage(),
                        status,
                        ZonedDateTime.now(ZoneId.of("Z"))
                );

        return new ResponseEntity<>(eventException, status);
    }
}
