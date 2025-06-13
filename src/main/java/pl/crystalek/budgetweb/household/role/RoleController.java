package pl.crystalek.budgetweb.household.role;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.role.permission.RolePermissionService;
import pl.crystalek.budgetweb.household.role.request.ChangeUserRoleRequest;
import pl.crystalek.budgetweb.household.role.request.CreateRoleRequest;
import pl.crystalek.budgetweb.household.role.request.DeleteRoleRequest;
import pl.crystalek.budgetweb.household.role.request.EditRoleRequest;
import pl.crystalek.budgetweb.household.role.request.EditRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.request.MakeRoleDefaultRequest;
import pl.crystalek.budgetweb.household.role.request.SaveRolePermissionsRequest;
import pl.crystalek.budgetweb.household.role.response.ChangeRoleResponse;
import pl.crystalek.budgetweb.household.role.response.ChangeUserRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.response.CreateRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.response.DeleteRoleResponse;
import pl.crystalek.budgetweb.household.role.response.GetRolePermissionResponse;
import pl.crystalek.budgetweb.household.role.response.MakeRoleDefaultResponse;
import pl.crystalek.budgetweb.household.role.response.RoleListResponse;
import pl.crystalek.budgetweb.household.role.response.SaveRolePermissionResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/household/roles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class RoleController {
    RoleService roleService;
    RolePermissionService rolePermissionService;

    @GetMapping("/changeRole")
    public Set<ChangeRoleResponse> getChangeRole(@AuthenticationPrincipal final long userId) {
        return roleService.getChangeRoleResponse(userId);
    }

    @GetMapping("/roleList")
    public Set<RoleListResponse> getRoleList(@AuthenticationPrincipal final long userId) {
        return roleService.getRoleListResponse(userId);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).ROLE_PERMISSIONS_EDIT)")
    @PatchMapping("/permissions/save")
    public ResponseEntity<ResponseAPI<SaveRolePermissionResponse>> savePermissions(
            @RequestBody @Valid final SaveRolePermissionsRequest saveRolePermissionsRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<SaveRolePermissionResponse> response = rolePermissionService.saveRolePermissions(saveRolePermissionsRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).ROLE_PERMISSIONS_VIEW)")
    @GetMapping("/permissions/{id}")
    public ResponseEntity<GetRolePermissionResponse> getRolePermissions(
            @AuthenticationPrincipal final long userId,
            @PathVariable final String id
    ) {
        final GetRolePermissionResponse response = rolePermissionService.getRolePermissions(id, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).ROLE_MAKE_DEFAULT)")
    @PostMapping("/makeDefault")
    public ResponseEntity<ResponseAPI<MakeRoleDefaultResponse>> makeRoleDefault(
            @RequestBody @Valid final MakeRoleDefaultRequest makeRoleDefaultRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<MakeRoleDefaultResponse> response = roleService.makeRoleDefault(makeRoleDefaultRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).ROLE_CHANGE)")
    @PostMapping("/changeRole")
    public ResponseEntity<ResponseAPI<ChangeUserRoleResponseMessage>> changeRole(
            @RequestBody @Valid final ChangeUserRoleRequest changeUserRoleRequest,
            @AuthenticationPrincipal final long requesterId
    ) {
        final ResponseAPI<ChangeUserRoleResponseMessage> response = roleService.changeUserRole(changeUserRoleRequest, requesterId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).ROLE_CREATE)")
    @PostMapping("/create")
    public ResponseEntity<ResponseAPI<CreateRoleResponseMessage>> createRole(
            @Validated(CreateRoleRequest.RoleNameRequestValidation.class) @RequestBody final CreateRoleRequest createRoleRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<CreateRoleResponseMessage> response = roleService.createRole(createRoleRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).ROLE_DELETE)")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseAPI<DeleteRoleResponse>> deleteRole(
            @Validated(DeleteRoleRequest.DeleteRoleRequestValidation.class) @RequestBody final DeleteRoleRequest deleteRoleRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<DeleteRoleResponse> response = roleService.deleteRole(deleteRoleRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).ROLE_EDIT)")
    @PatchMapping("/edit")
    public ResponseEntity<ResponseAPI<EditRoleResponseMessage>> editRole(
            @Validated({CreateRoleRequest.RoleNameRequestValidation.class, EditRoleRequest.EditRoleRequestValidation.class}) @RequestBody final EditRoleRequest editRoleRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<EditRoleResponseMessage> response = roleService.editRole(editRoleRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
