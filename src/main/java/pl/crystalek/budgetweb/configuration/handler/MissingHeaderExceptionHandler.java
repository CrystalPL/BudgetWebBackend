package pl.crystalek.budgetweb.configuration.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class MissingHeaderExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingRequestBody(final MissingRequestHeaderException ex) {
        return ResponseEntity
                .badRequest()
                .body(String.format("Required header '%s' is not present", ex.getHeaderName()));
    }
}
