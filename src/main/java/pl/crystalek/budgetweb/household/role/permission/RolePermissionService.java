package pl.crystalek.budgetweb.household.role.permission;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.role.Role;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RolePermissionService {
    RolePermissionRepository repository;

    public void addPermissionsToDefaultRole(final Role role) {
        final Set<Permission> permissions = EnumSet.of(Permission.HOUSEHOLD_INVITE_MEMBER);

        repository.saveAll(permissions.stream()
                .map(permission -> new RolePermission(role, permission))
                .collect(Collectors.toSet())
        );
    }

    public void addPermissionsToOwnerDefaultRole(final Role role) {
        repository.saveAll(Arrays.stream(Permission.values())
                .map(permission -> new RolePermission(role, permission))
                .collect(Collectors.toSet())
        );
    }

    @Cacheable(value = "userPermissions", key = "#userId")
    public Set<Permission> getUserPermissions(final long userId) {
        return repository.findPermissionNamesByUserId(userId);
    }
}
