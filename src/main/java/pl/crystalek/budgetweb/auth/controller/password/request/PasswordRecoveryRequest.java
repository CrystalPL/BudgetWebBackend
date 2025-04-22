package pl.crystalek.budgetweb.auth.controller.password.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordRecoveryRequest(
        @NotBlank(message = "MISSING_EMAIL")
        @Pattern(regexp = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", message = "INVALID_EMAIL")
        String emailToReset
) {
}
