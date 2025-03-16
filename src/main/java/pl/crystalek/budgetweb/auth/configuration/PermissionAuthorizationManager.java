package pl.crystalek.budgetweb.auth.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import pl.crystalek.budgetweb.household.role.permission.Permission;

import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    Permission permission;

    public static PermissionAuthorizationManager hasPermission(final Permission permission) {
        return new PermissionAuthorizationManager(permission);
    }

    @Override
    public AuthorizationDecision check(final Supplier<Authentication> authenticationSupplier, final RequestAuthorizationContext object) {
        final Authentication authentication = authenticationSupplier.get();
        final boolean checkedPermission = checkPermission(permission.getPermission(), ((Set<Permission>) authentication.getDetails()));
        return new AuthorizationDecision(checkedPermission);
    }

    public boolean checkPermission(final String permission, final Set<Permission> permissions) {
        for (final Permission permissionEnum : permissions) {
            final String havingPermission = permissionEnum.getPermission();
            if (havingPermission.equals("*")) {
                return true;
            }

            if (!havingPermission.endsWith(".*")) {
                if (havingPermission.equalsIgnoreCase(permission)) {
                    return true;
                }

                continue;
            }

            final String permBase = havingPermission.substring(0, havingPermission.length() - 1);
            if (permission.length() < permBase.length()) {
                continue;
            }

            if (!permBase.equalsIgnoreCase(permission.substring(0, permBase.length()))) {
                continue;
            }

            return true;
        }

        return false;
    }
}
