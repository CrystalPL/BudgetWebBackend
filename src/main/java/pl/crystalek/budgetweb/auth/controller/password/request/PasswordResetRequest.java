package pl.crystalek.budgetweb.auth.controller.password.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank(message = "MISSING_TOKEN")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "INVALID_TOKEN")
        String token,

        @NotBlank(message = "MISSING_PASSWORD")
        @Size(min = 8, message = "PASSWORD_TOO_SHORT")
        @Size(max = 255, message = "PASSWORD_TOO_LONG")
        @Pattern(regexp = ".*[A-Z].*", message = "MISSING_UPPERCASE")
        @Pattern(regexp = ".*[a-z].*", message = "MISSING_LOWERCASE")
        @Pattern(regexp = ".*\\d.*", message = "MISSING_NUMBER")
        @Pattern(regexp = ".*[!@#$%^&*].*", message = "MISSING_SPECIAL_CHAR")
        String password,

        @NotBlank(message = "MISSING_CONFIRM_PASSWORD")
        String confirmPassword
) {

    @AssertTrue(message = "PASSWORD_MISMATCH")
    private boolean isPasswordMatching() {
        return password.equals(confirmPassword);
    }
}
