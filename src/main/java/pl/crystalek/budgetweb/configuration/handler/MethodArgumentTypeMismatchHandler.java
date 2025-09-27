package pl.crystalek.budgetweb.configuration.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@ControllerAdvice
class MethodArgumentTypeMismatchHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String validValues = String.join(", ",
                    Arrays.stream(ex.getRequiredType().getEnumConstants())
                            .map(Object::toString)
                            .toArray(String[]::new));

            String message = String.format(
                    "Niepoprawna wartość '%s'. Dostępne wartości: [%s]",
                    ex.getValue(),
                    validValues
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
