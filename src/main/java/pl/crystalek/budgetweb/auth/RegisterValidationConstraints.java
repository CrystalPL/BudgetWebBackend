package pl.crystalek.budgetweb.auth;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class RegisterValidationConstraints implements Validator {
    static int USERNAME_MAX_LENGTH = 64;
    static int USERNAME_MIN_LENGTH = 3;
    static EmailValidationConstraints EMAIL_VALIDATION_CONSTRAINTS = new EmailValidationConstraints();
    static PasswordValidationConstraints PASSWORD_VALIDATION_CONSTRAINTS = new PasswordValidationConstraints();

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.REGISTER;
    }
}
