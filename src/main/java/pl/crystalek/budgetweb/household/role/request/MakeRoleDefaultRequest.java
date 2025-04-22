package pl.crystalek.budgetweb.household.role.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MakeRoleDefaultRequest(
        @NotNull(message = "MISSING_ROLE_ID")
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT")
        String roleId
) {

    public Long getRoleId() {
        return Long.parseLong(roleId);
    }
}
