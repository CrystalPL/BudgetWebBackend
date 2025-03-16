package pl.crystalek.budgetweb.household.role;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.role.model.EditRoleRequest;
import pl.crystalek.budgetweb.household.role.model.EditRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.model.RoleResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.time.Instant;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RoleService {
    RoleRepository repository;

    public Role createDefaultRole(final Household household) {
        final Role role = new Role(household, "Domownik", "#17d12a", Instant.now());

        return repository.save(role);
    }

    public Role createOwnerDefaultRole(final Household household) {
        final Role role = new Role(household, "Założyciel", "#d11717", Instant.now());

        return repository.save(role);
    }

    //Zwraca wszystkie role, oprócz roli właścicielskiej
    public Set<RoleResponse> getRoles(final long userId) {
        return repository.getRoles(userId);
    }

    @Transactional
    public ResponseAPI<EditRoleResponseMessage> editUserRole(final EditRoleRequest editRoleRequest, final long requesterId) {
        repository.editUserRole(editRoleRequest.getRoleId(), editRoleRequest.getMemberId());

        return new ResponseAPI<>(true, EditRoleResponseMessage.SUCCESS);
    }
}
