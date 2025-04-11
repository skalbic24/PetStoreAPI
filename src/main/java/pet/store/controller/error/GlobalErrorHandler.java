package pet.store.controller.error;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("Error: {}", ex.toString());
        return Map.of("message", ex.toString());
    }
}

