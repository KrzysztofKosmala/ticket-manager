package pl.ticket.feign.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class FeignExceptionHandler
{
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> handleFeignExceptionNotFound(FeignException.NotFound ex) {
        log.info("FeignException.NotFound has been thrown.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Element not found: " + ex.getMessage());
    }
}
