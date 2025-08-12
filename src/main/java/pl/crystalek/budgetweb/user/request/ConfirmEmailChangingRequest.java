package pl.crystalek.budgetweb.user.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ConfirmEmailChangingRequest(
        @NotBlank(message = "MISSING_TOKEN", groups = ValidationGroups.NotEmpty.class)
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "INVALID_TOKEN", groups = ValidationGroups.Format.class)
        String token
) {
    @GroupSequence({ValidationGroups.NotEmpty.class, ValidationGroups.Format.class})
    public interface ConfirmEmailChangingRequestValidation {}

    private interface ValidationGroups {
        interface NotEmpty {}

        interface Format {}

    }

}
