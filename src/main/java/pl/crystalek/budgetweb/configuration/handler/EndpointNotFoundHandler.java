package pl.crystalek.budgetweb.configuration.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@RestControllerAdvice
class EndpointNotFoundHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleMissingRequestBody(final NoHandlerFoundException ex) {
        return Map.of(
                "error", "Endpoint not found",
                "path", ex.getRequestURL()
        );
    }
}
