package pl.crystalek.budgetweb.household.role.response;

import lombok.Getter;
import pl.crystalek.budgetweb.household.role.permission.Permission;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@Getter
public class GetRolePermissionResponse extends ResponseAPI<GetRolePermissionResponseMessage> {
    private Set<Permission> permissions;

    public GetRolePermissionResponse(final boolean success, final GetRolePermissionResponseMessage message, final Set<Permission> permissions) {
        super(success, message);

        this.permissions = permissions;
    }

    public GetRolePermissionResponse(final boolean success, final GetRolePermissionResponseMessage message) {
        super(success, message);
    }

}
