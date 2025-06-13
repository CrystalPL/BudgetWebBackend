package pl.crystalek.budgetweb.household.role.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DeleteRoleRequest(
        @NotNull(message = "MISSING_ROLE_ID", groups = DeleteRoleRequest.ValidationGroups.MissingRoleId.class)
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT", groups = DeleteRoleRequest.ValidationGroups.ErrorNumberFormat.class)
        String roleId
) {
    public Long getRoleId() {
        return Long.parseLong(roleId);
    }

    @GroupSequence({DeleteRoleRequest.ValidationGroups.MissingRoleId.class, DeleteRoleRequest.ValidationGroups.ErrorNumberFormat.class})
    public interface DeleteRoleRequestValidation {}

    interface ValidationGroups {
        interface MissingRoleId {}

        interface ErrorNumberFormat {}
    }
}
