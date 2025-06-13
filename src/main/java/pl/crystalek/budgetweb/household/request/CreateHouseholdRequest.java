package pl.crystalek.budgetweb.household.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateHouseholdRequest(
        @NotBlank(message = "MISSING_NAME", groups = ValidationGroups.NameNotBlank.class)
        @Size(min = 2, message = "NAME_TOO_SHORT", groups = ValidationGroups.NameMinSize.class)
        @Size(max = 32, message = "NAME_TOO_LONG", groups = ValidationGroups.NameMaxSize.class)
        String name
) {
    @GroupSequence({ValidationGroups.NameNotBlank.class, ValidationGroups.NameMinSize.class, ValidationGroups.NameMaxSize.class})
    public interface Validation {}

    interface ValidationGroups {
        interface NameNotBlank {}

        interface NameMinSize {}

        interface NameMaxSize {}
    }
}
