package pl.crystalek.budgetweb.share.validation.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$");

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (StringUtils.isEmpty(email)) {
            addMessage(context, EmailValidationErrorType.MISSING_EMAIL);
            return false;
        }

        if (email.length() > 255) {
            addMessage(context, EmailValidationErrorType.EMAIL_TOO_LONG);
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            addMessage(context, EmailValidationErrorType.INVALID_EMAIL);
            return false;
        }

        return true;
    }

    private void addMessage(final ConstraintValidatorContext context, final EmailValidationErrorType message) {
        context.buildConstraintViolationWithTemplate(message.name()).addConstraintViolation();
    }
}