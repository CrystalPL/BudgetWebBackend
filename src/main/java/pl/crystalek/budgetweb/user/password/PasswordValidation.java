package pl.crystalek.budgetweb.user.password;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PasswordValidation {

    public PasswordValidationResult validatePassword(final String password) {
        if (password.length() < 8) {
            return PasswordValidationResult.SHORT_PASSWORD;
        }

        if (password.length() > 255) {
            return PasswordValidationResult.TOO_LONG_PASSWORD;
        }

        if (password.chars().noneMatch(Character::isUpperCase)) {
            return PasswordValidationResult.MISSING_UPPERCASE;
        }

        if (password.chars().noneMatch(Character::isLowerCase)) {
            return PasswordValidationResult.MISSING_LOWERCASE;
        }

        if (password.chars().noneMatch(Character::isDigit)) {
            return PasswordValidationResult.MISSING_NUMBER;
        }

        if (password.chars().noneMatch(ch -> "!@#$%^&*".indexOf(ch) >= 0)) {
            return PasswordValidationResult.MISSING_SPECIAL_CHAR;
        }

        return PasswordValidationResult.OK;
    }
}
