package pl.crystalek.budgetweb.household.role.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ChangeUserRoleRequest(
        @NotNull(message = "MISSING_MEMBER_ID")
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT")
        String memberId,

        @NotNull(message = "MISSING_ROLE_ID")
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT")
        String roleId
) {
    public Long getMemberId() {
        return Long.parseLong(memberId);
    }

    public Long getRoleId() {
        return Long.parseLong(roleId);
    }
}
