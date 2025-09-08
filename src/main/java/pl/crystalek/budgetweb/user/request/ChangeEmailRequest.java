package pl.crystalek.budgetweb.user.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeEmailRequest(
        @NotBlank(message = "MISSING_EMAIL", groups = ValidationGroups.MissingEmail.class)
        @Pattern(regexp = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", message = "INVALID_EMAIL", groups = ValidationGroups.InvalidEmail.class)
        String email,

        @NotBlank(message = "MISSING_CONFIRM_EMAIL", groups = ValidationGroups.MissingConfirmEmail.class)
        String confirmEmail,

        @NotBlank(message = "MISSING_PASSWORD", groups = ValidationGroups.MissingPassword.class)
        String password
) {

    @AssertTrue(message = "EMAIL_MISMATCH", groups = ValidationGroups.EmailMismatch.class)
    private boolean isEmailMatching() {
        return email.equals(confirmEmail);
    }

    @GroupSequence({ValidationGroups.MissingEmail.class, ValidationGroups.InvalidEmail.class, ValidationGroups.MissingConfirmEmail.class,
            ValidationGroups.MissingPassword.class, ValidationGroups.EmailMismatch.class})
    public interface ChangeEmailRequestValidation {}

    private interface ValidationGroups {
        interface MissingEmail {}

        interface InvalidEmail {}

        interface MissingConfirmEmail {}

        interface MissingPassword {}

        interface EmailMismatch {}
    }
}
