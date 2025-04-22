package pl.crystalek.budgetweb.configuration.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class MethodNotSupportedHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleMissingRequestBody(final HttpRequestMethodNotSupportedException ex) {
        return Map.of(
                "error", "Method is not supported",
                "method", ex.getMethod()
        );
    }
}
