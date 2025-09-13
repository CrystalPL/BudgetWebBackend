package pl.crystalek.budgetweb.user.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import pl.crystalek.budgetweb.share.validation.password.ValidPassword;

public record ChangePasswordRequest(
        @NotBlank(message = "MISSING_OLD_PASSWORD", groups = ValidationGroups.MissingOldPassword.class)
        String oldPassword,

        @ValidPassword(groups = ValidationGroups.MissingPassword.class)
        String password,

        @NotBlank(message = "MISSING_CONFIRM_PASSWORD", groups = ValidationGroups.MissingConfirmPassword.class)
        String confirmPassword
) {

    @AssertTrue(message = "PASSWORD_MISMATCH", groups = ValidationGroups.PasswordMismatch.class)
    private boolean isPasswordMatching() {
        return password.equals(confirmPassword);
    }

    @GroupSequence({ValidationGroups.MissingOldPassword.class, ValidationGroups.MissingPassword.class, ValidationGroups.MissingConfirmPassword.class, ValidationGroups.PasswordMismatch.class})
    public interface ChangePasswordRequestValidation {}

    private interface ValidationGroups {
        interface MissingOldPassword {}

        interface MissingPassword {}

        interface MissingConfirmPassword {}

        interface PasswordMismatch {}
    }
}
