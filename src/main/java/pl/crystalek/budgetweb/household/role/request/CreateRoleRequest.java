package pl.crystalek.budgetweb.household.role.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
        @NotBlank(message = "MISSING_NAME", groups = CreateRoleRequest.ValidationGroups.NameNotBlank.class)
        @Size(min = 2, message = "NAME_TOO_SHORT", groups = CreateRoleRequest.ValidationGroups.NameMinSize.class)
        @Size(max = 32, message = "NAME_TOO_LONG", groups = CreateRoleRequest.ValidationGroups.NameMaxSize.class)
        String name,

        @NotBlank(message = "MISSING_COLOR", groups = CreateRoleRequest.ValidationGroups.MissingColor.class)
        @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "INVALID_COLOR_FORMAT", groups = CreateRoleRequest.ValidationGroups.InvalidColorFormat.class)
        String color
) {
    @GroupSequence({CreateRoleRequest.ValidationGroups.NameNotBlank.class, CreateRoleRequest.ValidationGroups.NameMinSize.class,
            CreateRoleRequest.ValidationGroups.NameMaxSize.class, ValidationGroups.MissingColor.class, ValidationGroups.InvalidColorFormat.class})
    public interface RoleNameRequestValidation {}

    interface ValidationGroups {
        interface NameNotBlank {}

        interface NameMinSize {}

        interface NameMaxSize {}

        interface MissingColor {}

        interface InvalidColorFormat {}
    }
}
