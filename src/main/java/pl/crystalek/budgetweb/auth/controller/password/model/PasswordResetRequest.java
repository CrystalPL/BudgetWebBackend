package pl.crystalek.budgetweb.auth.controller.password.model;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank(message = "Token cannot be blank") String token, String password, String confirmPassword) {
}
