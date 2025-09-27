package pl.crystalek.budgetweb.auth;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class EmailValidationConstraints implements Validator {
    static int EMAIL_MAX_LENGTH = 255;
    static String EMAIL_REGEX = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$";

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.EMAIL;
    }
}
