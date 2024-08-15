package pl.ticket.event.admin.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler
{

    @ExceptionHandler(value = {InvalidRequestedDataException.class})
    public ResponseEntity<Object> handleInvalidRequestedDataException(InvalidRequestedDataException ex)
    {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;

        ApiException apiException = new ApiException
                (
                        ex.getMessage(),
                        status,
                        ZonedDateTime.now(ZoneId.of("Z"))
                );

        return new ResponseEntity<>(apiException, status);
    }
}
