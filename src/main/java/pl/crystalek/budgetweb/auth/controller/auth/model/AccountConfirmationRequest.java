package pl.crystalek.budgetweb.auth.controller.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountConfirmationRequest(
        @NotBlank(message = "MISSING_TOKEN")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "INVALID_TOKEN")
        String confirmationToken
) {
}
