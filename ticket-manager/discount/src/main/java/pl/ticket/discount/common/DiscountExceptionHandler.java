package pl.ticket.discount.common;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class DiscountExceptionHandler
{

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found: " + ex.getMessage());
    }
}