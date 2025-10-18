package pl.crystalek.budgetweb.user.validator.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (StringUtils.isEmpty(email)) {
            addMessage(context, EmailValidationErrorType.MISSING_EMAIL);
            return false;
        }

        if (email.length() > EmailValidationConstraints.EMAIL_MAX_LENGTH) {
            addMessage(context, EmailValidationErrorType.EMAIL_TOO_LONG);
            return false;
        }

        if (!email.matches(EmailValidationConstraints.EMAIL_REGEX)) {
            addMessage(context, EmailValidationErrorType.INVALID_EMAIL);
            return false;
        }

        return true;
    }

    private void addMessage(final ConstraintValidatorContext context, final EmailValidationErrorType message) {
        context.buildConstraintViolationWithTemplate(message.name()).addConstraintViolation();
    }
}