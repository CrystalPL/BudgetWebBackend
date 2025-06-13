package pl.crystalek.budgetweb.configuration.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class MissingBodyExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleMissingRequestBody(final HttpMessageNotReadableException exception) {
        final Throwable cause = exception.getCause();

        if (cause.getClass().equals(InvalidFormatException.class)) {
            return handleInvalidFormat();
        }

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Request body is missing or malformed");
    }

    private ResponseEntity<Object> handleInvalidFormat() {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "success", "false",
                        "message", "INVALID_NUMBER_FORMAT"
                ));
    }
}
