package pl.crystalek.budgetweb.user.controller.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeEmailRequest(
        @NotBlank(message = "MISSING_EMAIL")
        @Pattern(regexp = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", message = "INVALID_EMAIL")
        String email,

        @NotBlank(message = "MISSING_CONFIRM_EMAIL")
        String confirmEmail,

        @NotBlank(message = "MISSING_PASSWORD")
        String password
) {

    @AssertTrue(message = "EMAIL_MISMATCH")
    private boolean isPasswordMatching() {
        return email.equals(confirmEmail);
    }
}
