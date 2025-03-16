package pl.crystalek.budgetweb.household.role;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.role.model.EditRoleRequest;
import pl.crystalek.budgetweb.household.role.model.EditRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.model.RoleResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/household/roles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class RoleController {
    RoleService roleService;

    @GetMapping()
    private Set<RoleResponse> getRoles() {
        //TODO ZWRACANIE BADREQUEST GDY NIE MA GOSPODARSTWA
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return roleService.getRoles(userId);
    }

    @PostMapping("/editRole")
    private ResponseEntity<ResponseAPI<EditRoleResponseMessage>> editRole(@RequestBody @Valid final EditRoleRequest editRoleRequest) {
        final long requesterId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<EditRoleResponseMessage> response = roleService.editUserRole(editRoleRequest, requesterId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
