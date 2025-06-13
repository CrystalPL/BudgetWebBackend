package pl.crystalek.budgetweb.auth.controller.auth.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "MISSING_USERNAME", groups = ValidationGroups.Username.class)
        @Size(max = 64, message = "TOO_LONG_USERNAME", groups = ValidationGroups.Username.class)
        String username,

        @NotBlank(message = "MISSING_EMAIL", groups = ValidationGroups.Email.class)
        @Size(max = 255, message = "EMAIL_TOO_LONG", groups = ValidationGroups.Email.class)
        @Pattern(regexp = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", message = "INVALID_EMAIL", groups = ValidationGroups.InvalidEmail.class)
        String email,

        @NotBlank(message = "MISSING_CONFIRM_EMAIL", groups = ValidationGroups.MissingConfirmEmail.class)
        String confirmEmail,

        @NotBlank(message = "MISSING_PASSWORD", groups = ValidationGroups.Password.class)
        @Size(min = 8, message = "PASSWORD_TOO_SHORT", groups = ValidationGroups.PasswordLength.class)
        @Size(max = 255, message = "PASSWORD_TOO_LONG", groups = ValidationGroups.PasswordLength.class)
        @Pattern(regexp = ".*[A-Z].*", message = "MISSING_UPPERCASE", groups = ValidationGroups.PasswordUppercase.class)
        @Pattern(regexp = ".*[a-z].*", message = "MISSING_LOWERCASE", groups = ValidationGroups.PasswordLowercase.class)
        @Pattern(regexp = ".*\\d.*", message = "MISSING_NUMBER", groups = ValidationGroups.PasswordNumber.class)
        @Pattern(regexp = ".*[!@#$%^&*].*", message = "MISSING_SPECIAL_CHAR", groups = ValidationGroups.PasswordSpecialChar.class)
        String password,

        @NotBlank(message = "MISSING_CONFIRM_PASSWORD", groups = ValidationGroups.ConfirmPassword.class)
        String confirmPassword,

        @NotNull(message = "MISSING_RECEIVE_UPDATES", groups = ValidationGroups.ReceiveUpdates.class)
        Boolean receiveUpdates
) {
    @AssertTrue(message = "PASSWORD_MISMATCH", groups = ValidationGroups.PasswordMismatch.class)
    private boolean isPasswordMatching() {
        return password.equals(confirmPassword);
    }

    @AssertTrue(message = "EMAIL_MISMATCH", groups = ValidationGroups.ConfirmEmail.class)
    private boolean isEmailMatching() {
        return email.equals(confirmEmail);
    }

    @GroupSequence({ValidationGroups.Username.class, ValidationGroups.Email.class, ValidationGroups.InvalidEmail.class,
            ValidationGroups.MissingConfirmEmail.class, ValidationGroups.ConfirmEmail.class, ValidationGroups.Password.class,
            ValidationGroups.PasswordLength.class, ValidationGroups.PasswordUppercase.class, ValidationGroups.PasswordLowercase.class,
            ValidationGroups.PasswordNumber.class, ValidationGroups.PasswordSpecialChar.class, ValidationGroups.ConfirmPassword.class,
            ValidationGroups.PasswordMismatch.class, ValidationGroups.ReceiveUpdates.class})
    public interface RegisterRequestValidation {}

    private interface ValidationGroups {
        interface Username {}

        interface Email {}

        interface InvalidEmail {}

        interface ConfirmEmail {}

        interface Password {}

        interface ConfirmPassword {}

        interface ReceiveUpdates {}

        interface PasswordLength {}

        interface PasswordUppercase {}

        interface PasswordLowercase {}

        interface PasswordNumber {}

        interface PasswordSpecialChar {}

        interface PasswordMismatch {}

        interface MissingConfirmEmail {}
    }
}
