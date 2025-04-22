package pl.crystalek.budgetweb.configuration.handler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@ControllerAdvice
class MethodArgumentNotValidExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class) //TODO ten cały kod można wyjebać
    public MethodArgumentNotValidResponse methodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> System.out.println(fieldError.getDefaultMessage()));
        final String message = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("WHAT HAPPEN BLYAT?");

        return new MethodArgumentNotValidResponse(false, message);
    }

}
