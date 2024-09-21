package pl.crystalek.budgetweb.auth.controller.auth.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "MISSING_USERNAME")
        @Size(max = 64, message = "TOO_LONG_USERNAME")
        String username,

        @NotBlank(message = "MISSING_EMAIL")
        @Pattern(regexp = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", message = "INVALID_EMAIL")
        String email,

        @NotBlank(message = "MISSING_CONFIRM_EMAIL")
        String confirmEmail,

        @NotBlank(message = "MISSING_PASSWORD")
        @Size(min = 8, message = "PASSWORD_TOO_SHORT")
        @Size(max = 255, message = "PASSWORD_TOO_LONG")
        @Pattern(regexp = ".*[A-Z].*", message = "MISSING_UPPERCASE")
        @Pattern(regexp = ".*[a-z].*", message = "MISSING_LOWERCASE")
        @Pattern(regexp = ".*\\d.*", message = "MISSING_NUMBER")
        @Pattern(regexp = ".*[!@#$%^&*].*", message = "MISSING_SPECIAL_CHAR")
        String password,

        @NotBlank(message = "MISSING_CONFIRM_PASSWORD")
        String confirmPassword,

        @NotNull(message = "MISSING_RECEIVE_UPDATES")
        Boolean receiveUpdates
) {
    @AssertTrue(message = "PASSWORD_MISMATCH")
    private boolean isPasswordMatching() {
        return password.equals(confirmPassword);
    }

    @AssertTrue(message = "EMAIL_MISMATCH")
    private boolean isEmailMatching() {
        return email.equals(confirmEmail);
    }
}
