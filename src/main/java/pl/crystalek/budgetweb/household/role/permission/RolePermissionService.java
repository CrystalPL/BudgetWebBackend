package pl.crystalek.budgetweb.household.role.permission;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.CreateHouseholdEvent;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.role.Role;
import pl.crystalek.budgetweb.household.role.RoleService;
import pl.crystalek.budgetweb.household.role.request.SaveRolePermissionsRequest;
import pl.crystalek.budgetweb.household.role.response.GetRolePermissionResponse;
import pl.crystalek.budgetweb.household.role.response.GetRolePermissionResponseMessage;
import pl.crystalek.budgetweb.household.role.response.SaveRolePermissionResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RolePermissionService {
    RolePermissionRepository repository;
    RoleService roleService;
    CacheManager cacheManager;

    @EventListener
    @Order(2)
    @CacheEvict(value = "userPermissions", key = "#event.household().owner.id")
    public void addPermissionsToOwnerRoleWhenHouseholdIsCreating(final CreateHouseholdEvent event) {
        final Role ownerRole = event.household().getOwnerRole();

        repository.saveAll(Arrays.stream(Permission.values())
                .map(permission -> new RolePermission(ownerRole, permission))
                .collect(Collectors.toSet())
        );
    }

    @Cacheable(value = "userPermissions", key = "#userId")
    public Set<Permission> getUserPermissions(final long userId) {
        return repository.findPermissionNamesByUserId(userId);
    }

    public GetRolePermissionResponse getRolePermissions(final String stringRoleId, final long requesterId) {
        if (stringRoleId == null || stringRoleId.isBlank()) {
            return new GetRolePermissionResponse(false, GetRolePermissionResponseMessage.MISSING_ROLE_ID);
        }

        final long roleId;
        try {
            roleId = Long.parseLong(stringRoleId);
        } catch (final NumberFormatException exception) {
            return new GetRolePermissionResponse(false, GetRolePermissionResponseMessage.ERROR_NUMBER_FORMAT);
        }

        final Optional<Role> roleOptional = roleService.getRole(roleId);
        if (roleOptional.isEmpty()) {
            return new GetRolePermissionResponse(false, GetRolePermissionResponseMessage.ROLE_NOT_FOUND);
        }

        final Role role = roleOptional.get();
        final Household household = role.getHousehold();

        final boolean anotherHousehold = household.getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (anotherHousehold) {
            return new GetRolePermissionResponse(false, GetRolePermissionResponseMessage.NOT_YOUR_HOUSEHOLD);
        }

        final Set<Permission> permissions = role.getPermissionSet().stream().map(RolePermission::getPermissionName).collect(Collectors.toSet());
        return new GetRolePermissionResponse(true, GetRolePermissionResponseMessage.SUCCESS, permissions);
    }

    public ResponseAPI<SaveRolePermissionResponse> saveRolePermissions(final SaveRolePermissionsRequest saveRolePermissionsRequest, final long requesterId) {
        final Optional<Role> roleOptional = roleService.getRole(saveRolePermissionsRequest.getRoleId());
        if (roleOptional.isEmpty()) {
            return new ResponseAPI<>(false, SaveRolePermissionResponse.ROLE_NOT_FOUND);
        }

        final Role role = roleOptional.get();
        final Household household = role.getHousehold();

        final boolean anotherHousehold = household.getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (anotherHousehold) {
            return new ResponseAPI<>(false, SaveRolePermissionResponse.NOT_YOUR_HOUSEHOLD);
        }

        repository.deletePermissions(role.getId(), saveRolePermissionsRequest.permissions());

        final List<Permission> permissionList = role.getPermissionSet().stream().map(RolePermission::getPermissionName).toList();
        final List<RolePermission> rolePermissions = saveRolePermissionsRequest.permissions().stream()
                .filter(permission -> !permissionList.contains(permission))
                .map(permission -> new RolePermission(role, permission))
                .toList();

        repository.saveAll(rolePermissions);

        final Cache cache = cacheManager.getCache("userPermissions");
        role.getMembersWithRole().forEach(member -> cache.evictIfPresent(member.getUser().getId()));

        return new ResponseAPI<>(true, SaveRolePermissionResponse.SUCCESS);
    }
}
