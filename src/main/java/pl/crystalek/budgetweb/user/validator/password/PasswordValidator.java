package pl.crystalek.budgetweb.user.validator.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (StringUtils.isEmpty(password)) {
            addMessage(context, PasswordValidationErrorType.MISSING_PASSWORD);
            return false;
        }

        if (password.length() < PasswordValidationConstraints.PASSWORD_MIN_LENGTH) {
            addMessage(context, PasswordValidationErrorType.PASSWORD_TOO_SHORT);
            return false;
        }

        if (password.length() > PasswordValidationConstraints.PASSWORD_MAX_LENGTH) {
            addMessage(context, PasswordValidationErrorType.PASSWORD_TOO_LONG);
            return false;
        }

        if (!password.matches(PasswordValidationConstraints.UPPERCASE_REGEX)) {
            addMessage(context, PasswordValidationErrorType.MISSING_UPPERCASE);
            return false;
        }

        if (!password.matches(PasswordValidationConstraints.LOWERCASE_REGEX)) {
            addMessage(context, PasswordValidationErrorType.MISSING_LOWERCASE);
            return false;
        }

        if (!password.matches(PasswordValidationConstraints.NUMBER_REGEX)) {
            addMessage(context, PasswordValidationErrorType.MISSING_NUMBER);
            return false;
        }

        if (!password.matches(PasswordValidationConstraints.SPECIAL_CHARACTERS)) {
            addMessage(context, PasswordValidationErrorType.MISSING_SPECIAL_CHAR);
            return false;
        }

        return true;
    }

    private void addMessage(final ConstraintValidatorContext context, final PasswordValidationErrorType message) {
        context.buildConstraintViolationWithTemplate(message.name()).addConstraintViolation();
    }
}