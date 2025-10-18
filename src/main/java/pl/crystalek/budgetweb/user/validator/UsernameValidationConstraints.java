package pl.crystalek.budgetweb.user.validator;

import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
public class UsernameValidationConstraints implements Validator {
    public static final int USERNAME_MAX_LENGTH = 32;

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.USERNAME;
    }
}
