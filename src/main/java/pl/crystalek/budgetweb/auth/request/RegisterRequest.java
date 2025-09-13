package pl.crystalek.budgetweb.auth.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.crystalek.budgetweb.share.validation.email.ValidEmail;
import pl.crystalek.budgetweb.share.validation.password.ValidPassword;

public record RegisterRequest(
        @NotBlank(message = "MISSING_USERNAME", groups = ValidationGroups.Username.class)
        @Size(max = 64, message = "TOO_LONG_USERNAME", groups = ValidationGroups.Username.class)
        String username,

        @ValidEmail(groups = ValidationGroups.Email.class)
        String email,

        @NotBlank(message = "MISSING_CONFIRM_EMAIL", groups = ValidationGroups.MissingConfirmEmail.class)
        String confirmEmail,

        @ValidPassword(groups = ValidationGroups.Password.class)
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

    @GroupSequence({ValidationGroups.Username.class, ValidationGroups.Email.class,
            ValidationGroups.MissingConfirmEmail.class, ValidationGroups.ConfirmEmail.class, ValidationGroups.Password.class,
            ValidationGroups.ConfirmPassword.class, ValidationGroups.PasswordMismatch.class, ValidationGroups.ReceiveUpdates.class})
    public interface RegisterRequestValidation {}

    private interface ValidationGroups {
        interface Username {}

        interface Email {}

        interface ConfirmEmail {}

        interface Password {}

        interface ConfirmPassword {}

        interface ReceiveUpdates {}

        interface PasswordMismatch {}

        interface MissingConfirmEmail {}
    }
}
