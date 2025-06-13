package pl.crystalek.budgetweb.household.role;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.household.CreateHouseholdEvent;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.household.role.request.ChangeUserRoleRequest;
import pl.crystalek.budgetweb.household.role.request.CreateRoleRequest;
import pl.crystalek.budgetweb.household.role.request.DeleteRoleRequest;
import pl.crystalek.budgetweb.household.role.request.EditRoleRequest;
import pl.crystalek.budgetweb.household.role.request.EditRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.request.MakeRoleDefaultRequest;
import pl.crystalek.budgetweb.household.role.response.ChangeRoleResponse;
import pl.crystalek.budgetweb.household.role.response.ChangeUserRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.response.CreateRoleResponseMessage;
import pl.crystalek.budgetweb.household.role.response.DeleteRoleResponse;
import pl.crystalek.budgetweb.household.role.response.MakeRoleDefaultResponse;
import pl.crystalek.budgetweb.household.role.response.RoleListResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RoleService {
    RoleRepository repository;
    UserService userService;
    CacheManager cacheManager;

    public Optional<Role> getRole(final long roleId) {
        return repository.findById(roleId);
    }

    @EventListener
    @Order(1)
    public void addDefaultRoleToHousehold(final CreateHouseholdEvent event) {
        final Household household = event.household();

        final Role defaultRole = new Role(household, "Domownik", "#17d12a", Instant.now());
        final Role ownerRole = new Role(household, "Założyciel", "#d11717", Instant.now());

        final Role proxyDefaultRole = repository.save(defaultRole);
        final Role proxyOwnerRole = repository.save(ownerRole);

        household.setDefaultRole(proxyDefaultRole);
        household.setOwnerRole(proxyOwnerRole);
    }

    @Transactional
    public ResponseAPI<MakeRoleDefaultResponse> makeRoleDefault(final MakeRoleDefaultRequest makeRoleDefaultRequest, final long requesterId) {
        final long roleId = makeRoleDefaultRequest.getRoleId();
        final Optional<Role> roleOptional = repository.findById(roleId);
        if (roleOptional.isEmpty()) {
            return new ResponseAPI<>(false, MakeRoleDefaultResponse.ROLE_NOT_FOUND);
        }

        final Role role = roleOptional.get();
        final Household household = role.getHousehold();

        final boolean anotherHousehold = household.getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (anotherHousehold) {
            return new ResponseAPI<>(false, MakeRoleDefaultResponse.NOT_YOUR_HOUSEHOLD);
        }

        if (household.getDefaultRole().getId() == roleId) {
            return new ResponseAPI<>(false, MakeRoleDefaultResponse.ROLE_ALREADY_DEFAULT);
        }

        if (household.getOwnerRole().getId() == roleId) {
            return new ResponseAPI<>(false, MakeRoleDefaultResponse.OWNER_ROLE_CANNOT_DEFAULT);
        }


        repository.editHouseholdDefaultRole(roleId, household.getId());
        return new ResponseAPI<>(true, MakeRoleDefaultResponse.SUCCESS);
    }

    public ResponseAPI<CreateRoleResponseMessage> createRole(final CreateRoleRequest request, final long requesterId) {
        final Household household = userService.getUserById(requesterId).get().getHouseholdMember().getHousehold();
        final Role role = new Role(household, request.name(), request.color(), Instant.now());

        try {
            repository.save(role);
        } catch (final DataIntegrityViolationException exception) {
            return new ResponseAPI<>(false, CreateRoleResponseMessage.ROLE_EXISTS);
        }

        return new ResponseAPI<>(true, CreateRoleResponseMessage.SUCCESS);
    }

    public Set<ChangeRoleResponse> getChangeRoleResponse(final long userId) {
        return repository.getChangeRoleResponse(userId);
    }

    public Set<RoleListResponse> getRoleListResponse(final long userId) {
        return repository.getRoleListResponse(userId);
    }

    public ResponseAPI<DeleteRoleResponse> deleteRole(final DeleteRoleRequest deleteRoleRequest, final long requesterId) {
        final Optional<Role> roleOptional = repository.findById(deleteRoleRequest.getRoleId());
        if (roleOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteRoleResponse.ROLE_NOT_FOUND);
        }

        final Role role = roleOptional.get();
        final Household household = role.getHousehold();
        final boolean anotherHousehold = household.getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (anotherHousehold) {
            return new ResponseAPI<>(false, DeleteRoleResponse.NOT_YOUR_HOUSEHOLD);
        }

        if (household.getOwnerRole().equals(role)) {
            return new ResponseAPI<>(false, DeleteRoleResponse.CANNOT_DELETE_OWNER_ROLE);
        }

        if (household.getDefaultRole().equals(role)) {
            return new ResponseAPI<>(false, DeleteRoleResponse.CANNOT_DELETE_DEFAULT_ROLE);
        }

        final Cache cache = cacheManager.getCache("userPermissions");
        for (final HouseholdMember member : role.getMembersWithRole()) {
            member.setRole(household.getDefaultRole());
            cache.evictIfPresent(member.getUser().getId());
        }

        repository.delete(role);

        return new ResponseAPI<>(true, DeleteRoleResponse.SUCCESS);
    }

    public ResponseAPI<EditRoleResponseMessage> editRole(final EditRoleRequest request, final long requesterId) {
        final Optional<Role> roleOptional = repository.findById(request.getRoleId());
        if (roleOptional.isEmpty()) {
            return new ResponseAPI<>(false, EditRoleResponseMessage.ROLE_NOT_FOUND);
        }

        final Role role = roleOptional.get();
        final boolean anotherHousehold = role.getHousehold().getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (anotherHousehold) {
            return new ResponseAPI<>(false, EditRoleResponseMessage.NOT_YOUR_HOUSEHOLD);
        }

        role.setName(request.name());
        role.setColor(request.color());

        try {
            repository.save(role);
        } catch (final DataIntegrityViolationException exception) {
            return new ResponseAPI<>(false, EditRoleResponseMessage.ROLE_NAME_EXISTS);
        }

        return new ResponseAPI<>(true, EditRoleResponseMessage.SUCCESS);
    }

    @Transactional
    @CacheEvict(value = "userPermissions", key = "#changeUserRoleRequest.roleId")
    public ResponseAPI<ChangeUserRoleResponseMessage> changeUserRole(final ChangeUserRoleRequest changeUserRoleRequest, final long requesterId) {
        final Optional<User> memberUserOptional = userService.getUserById(changeUserRoleRequest.getMemberId());
        if (memberUserOptional.isEmpty()) {
            return new ResponseAPI<>(false, ChangeUserRoleResponseMessage.USER_NOT_FOUND);
        }

        final User user = memberUserOptional.get();
        final HouseholdMember householdMember = user.getHouseholdMember();
        if (householdMember == null) {
            return new ResponseAPI<>(false, ChangeUserRoleResponseMessage.DIFFERENT_HOUSEHOLD);
        }

        final Household household = householdMember.getHousehold();
        final boolean sameHousehold = household.getMembers().stream().anyMatch(member -> member.getUser().getId() == requesterId);
        if (!sameHousehold) {
            return new ResponseAPI<>(false, ChangeUserRoleResponseMessage.DIFFERENT_HOUSEHOLD);
        }

        final boolean roleExists = household.getRoles().stream().anyMatch(role -> role.getId() == changeUserRoleRequest.getRoleId());
        if (!roleExists) {
            return new ResponseAPI<>(false, ChangeUserRoleResponseMessage.ROLE_NOT_FOUND);
        }

        final Long roleId = changeUserRoleRequest.getRoleId();
        //nie można ustawiać roli właściciela
        if (household.getOwnerRole().getId() == roleId) {
            return new ResponseAPI<>(false, ChangeUserRoleResponseMessage.SET_OWNER_ROLE_ERROR);
        }

        //nie można zmienić roli właścicielowi gospodarstwa
        if (changeUserRoleRequest.getMemberId() == household.getOwner().getId()) {
            return new ResponseAPI<>(false, ChangeUserRoleResponseMessage.CHANGE_OWNER_ROLE);
        }

        repository.changeUserRole(changeUserRoleRequest.getRoleId(), changeUserRoleRequest.getMemberId());

        return new ResponseAPI<>(true, ChangeUserRoleResponseMessage.SUCCESS);
    }
}
