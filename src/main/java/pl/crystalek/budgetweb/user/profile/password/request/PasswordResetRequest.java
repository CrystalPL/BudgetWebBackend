package pl.crystalek.budgetweb.user.profile.password.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.StringUtils;
import pl.crystalek.budgetweb.user.validator.password.ValidPassword;

public record PasswordResetRequest(
        @NotBlank(message = "MISSING_TOKEN", groups = ValidationGroups.MissingToken.class)
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "INVALID_TOKEN", groups = ValidationGroups.InvalidToken.class)
        String token,

        @ValidPassword(groups = ValidationGroups.Password.class)
        String password,

        @NotBlank(message = "MISSING_CONFIRM_PASSWORD", groups = ValidationGroups.EmptyConfirmPassword.class)
        String confirmPassword
) {

    @AssertTrue(message = "PASSWORD_MISMATCH", groups = ValidationGroups.PasswordMatch.class)
    private boolean isPasswordMatching() {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmPassword)) {
            return false;
        }

        return password.equals(confirmPassword);
    }

    @GroupSequence({ValidationGroups.MissingToken.class, ValidationGroups.Password.class, ValidationGroups.InvalidToken.class,
            ValidationGroups.EmptyConfirmPassword.class, ValidationGroups.PasswordMatch.class})
    public interface PasswordResetRequestValidation {}

    private interface ValidationGroups {
        interface MissingToken {}

        interface InvalidToken {}

        interface Password {}

        interface EmptyConfirmPassword {}

        interface PasswordMatch {}
    }
}
