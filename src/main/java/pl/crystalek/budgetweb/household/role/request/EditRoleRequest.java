package pl.crystalek.budgetweb.household.role.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditRoleRequest(
        @NotNull(message = "MISSING_ROLE_ID")
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT")
        String roleId,

        @NotBlank(message = "MISSING_NAME", groups = CreateRoleRequest.ValidationGroups.NameNotBlank.class)
        @Size(min = 2, message = "NAME_TOO_SHORT", groups = CreateRoleRequest.ValidationGroups.NameMinSize.class)
        @Size(max = 32, message = "NAME_TOO_LONG", groups = CreateRoleRequest.ValidationGroups.NameMaxSize.class)
        String name,

        @NotBlank(message = "MISSING_COLOR", groups = CreateRoleRequest.ValidationGroups.MissingColor.class)
        @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "INVALID_COLOR_FORMAT", groups = CreateRoleRequest.ValidationGroups.InvalidColorFormat.class)
        String color
) {

    public Long getRoleId() {
        return Long.parseLong(roleId);
    }

    @GroupSequence({EditRoleRequest.ValidationGroups.MissingRoleId.class, EditRoleRequest.ValidationGroups.ErrorNumberFormat.class})
    public interface EditRoleRequestValidation {}

    interface ValidationGroups {
        interface MissingRoleId {}

        interface ErrorNumberFormat {}
    }
}
