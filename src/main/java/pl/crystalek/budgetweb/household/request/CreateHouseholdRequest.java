package pl.crystalek.budgetweb.household.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pl.crystalek.budgetweb.household.constraints.HouseholdNameConstraints;

public record CreateHouseholdRequest(
        @NotBlank(message = "MISSING_NAME", groups = ValidationGroups.NameNotBlank.class)
        @Size(min = HouseholdNameConstraints.HOUSEHOLD_NAME_MIN_LENGTH, message = "NAME_TOO_SHORT", groups = ValidationGroups.NameMinSize.class)
        @Size(max = HouseholdNameConstraints.HOUSEHOLD_NAME_MAX_LENGTH, message = "NAME_TOO_LONG", groups = ValidationGroups.NameMaxSize.class)
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
