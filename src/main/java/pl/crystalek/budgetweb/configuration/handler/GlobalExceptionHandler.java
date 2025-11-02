package pl.crystalek.budgetweb.configuration.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.crystalek.budgetweb.exception.BudgetAppException;

import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BudgetAppException.class)
    public Object handleValidationException(final BudgetAppException exception) {
        return Map.of(
                "success", false,
                "message", exception.getMessage()
        );
    }
}
