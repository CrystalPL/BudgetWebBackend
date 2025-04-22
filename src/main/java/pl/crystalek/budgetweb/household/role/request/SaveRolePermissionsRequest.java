package pl.crystalek.budgetweb.household.role.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import pl.crystalek.budgetweb.household.role.permission.Permission;

import java.util.Set;

public record SaveRolePermissionsRequest(
        @NotNull(message = "MISSING_ROLE_ID")
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT")
        String roleId,

        @NotNull(message = "MISSING_PERMISSIONS")
        Set<Permission> permissions
) {

    public Long getRoleId() {
        return Long.parseLong(roleId);
    }
}
