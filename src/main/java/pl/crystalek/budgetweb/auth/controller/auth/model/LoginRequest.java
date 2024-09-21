package pl.crystalek.budgetweb.auth.controller.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank(message = "MISSING_EMAIL")
        String email,

        @NotBlank(message = "MISSING_PASSWORD")
        String password,

        @NotNull(message = "MISSING_REMEMBER_ME")
        Boolean rememberMe
) {
}
