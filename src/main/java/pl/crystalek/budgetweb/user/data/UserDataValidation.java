package pl.crystalek.budgetweb.user.data;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterRequest;

import java.util.regex.Pattern;

@UtilityClass
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDataValidation {
    Pattern EMAIL_PATTERN = Pattern.compile("^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$");

    public UserDataValidationResult validateUserData(final RegisterRequest registerRequest) {
        final String username = registerRequest.username();
        final String email = registerRequest.email();
        final String confirmEmail = registerRequest.confirmEmail();
        final String password = registerRequest.password();
        final String confirmPassword = registerRequest.confirmPassword();

        if (username == null || username.isEmpty()) {
            return UserDataValidationResult.MISSING_USERNAME;
        }

        if (username.length() > 64) {
            return UserDataValidationResult.TOO_LONG_USERNAME;
        }

        if (email == null || email.isEmpty()) {
            return UserDataValidationResult.MISSING_EMAIL;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return UserDataValidationResult.INVALID_EMAIL;
        }

        if (email.length() > 255) {
            return UserDataValidationResult.TOO_LONG_EMAIL;
        }

        if (confirmEmail == null || confirmEmail.isEmpty()) {
            return UserDataValidationResult.MISSING_CONFIRM_EMAIL;
        }

        if (!email.equals(confirmEmail)) {
            return UserDataValidationResult.EMAIL_MISMATCH;
        }

        if (password == null || password.isEmpty()) {
            return UserDataValidationResult.MISSING_PASSWORD;
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return UserDataValidationResult.MISSING_CONFIRM_PASSWORD;
        }

        if (!password.equals(confirmPassword)) {
            return UserDataValidationResult.PASSWORD_MISMATCH;
        }

        return UserDataValidationResult.OK;
    }
}
