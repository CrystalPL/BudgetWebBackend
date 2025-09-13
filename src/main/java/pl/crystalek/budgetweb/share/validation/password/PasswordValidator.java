package pl.crystalek.budgetweb.share.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (StringUtils.isEmpty(password)) {
            addMessage(context, PasswordValidationErrorType.MISSING_PASSWORD);
            return false;
        }

        if (password.length() < 8) {
            addMessage(context, PasswordValidationErrorType.PASSWORD_TOO_SHORT);
            return false;
        }

        if (password.length() > 255) {
            addMessage(context, PasswordValidationErrorType.PASSWORD_TOO_LONG);
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            addMessage(context, PasswordValidationErrorType.MISSING_UPPERCASE);
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            addMessage(context, PasswordValidationErrorType.MISSING_LOWERCASE);
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            addMessage(context, PasswordValidationErrorType.MISSING_NUMBER);
            return false;
        }

        if (!password.matches(".*[!@#$%^&*].*")) {
            addMessage(context, PasswordValidationErrorType.MISSING_SPECIAL_CHAR);
            return false;
        }

        return true;
    }

    private void addMessage(final ConstraintValidatorContext context, final PasswordValidationErrorType message) {
        context.buildConstraintViolationWithTemplate(message.name()).addConstraintViolation();
    }
}