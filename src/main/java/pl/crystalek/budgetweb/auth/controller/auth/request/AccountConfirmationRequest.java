package pl.crystalek.budgetweb.auth.controller.auth.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountConfirmationRequest(
        @NotBlank(message = "MISSING_TOKEN", groups = ValidationGroups.MissingToken.class)
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "INVALID_TOKEN", groups = ValidationGroups.InvalidToken.class)
        String confirmationToken
) {

    @GroupSequence({ValidationGroups.MissingToken.class, ValidationGroups.InvalidToken.class})
    public interface AccountConfirmationValidation {}

    private interface ValidationGroups {
        interface MissingToken {}

        interface InvalidToken {}
    }
}