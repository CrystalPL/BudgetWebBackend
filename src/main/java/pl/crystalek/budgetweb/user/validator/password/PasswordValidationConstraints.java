package pl.crystalek.budgetweb.user.validator.password;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
class PasswordValidationConstraints implements Validator {
    static int PASSWORD_MIN_LENGTH = 8;
    static int PASSWORD_MAX_LENGTH = 255;
    static String UPPERCASE_REGEX = ".*[A-Z].*";
    static String LOWERCASE_REGEX = ".*[a-z].*";
    static String NUMBER_REGEX = ".*\\d.*";
    static String SPECIAL_CHARACTERS = ".*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~].*";

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.PASSWORD;
    }
}
