package pl.crystalek.budgetweb.household.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeHouseholdNameRequest(
        @NotBlank(message = "MISSING_NAME", groups = CreateHouseholdRequest.ValidationGroups.NameNotBlank.class)
        @Size(min = 2, message = "NAME_TOO_SHORT", groups = CreateHouseholdRequest.ValidationGroups.NameMinSize.class)
        @Size(max = 32, message = "NAME_TOO_LONG", groups = CreateHouseholdRequest.ValidationGroups.NameMaxSize.class)
        String householdName
) {
    @GroupSequence({CreateHouseholdRequest.ValidationGroups.NameNotBlank.class, CreateHouseholdRequest.ValidationGroups.NameMinSize.class, CreateHouseholdRequest.ValidationGroups.NameMaxSize.class})
    public interface HouseholdNameRequestValidation {}

    interface ValidationGroups {
        interface NameNotBlank {}

        interface NameMinSize {}

        interface NameMaxSize {}
    }
}
