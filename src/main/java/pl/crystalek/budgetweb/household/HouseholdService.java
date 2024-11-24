package pl.crystalek.budgetweb.household;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.household.member.HouseholdMemberService;
import pl.crystalek.budgetweb.household.model.CreateHouseholdRequest;
import pl.crystalek.budgetweb.household.model.CreateHouseholdResponseMessage;
import pl.crystalek.budgetweb.household.role.Role;
import pl.crystalek.budgetweb.household.role.RoleService;
import pl.crystalek.budgetweb.household.role.permission.RolePermissionService;
import pl.crystalek.budgetweb.log.EventLog;
import pl.crystalek.budgetweb.log.EventLogBuilder;
import pl.crystalek.budgetweb.log.EventLogService;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.User;
import pl.crystalek.budgetweb.user.UserService;

import java.time.Instant;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HouseholdService {
    HouseholdRepository repository;
    UserService userService;
    HouseholdMemberService householdMemberService;
    RoleService roleService;
    RolePermissionService rolePermissionService;
    EventLogService eventLogService;

    public ResponseAPI<CreateHouseholdResponseMessage> create(final CreateHouseholdRequest createHouseholdRequest, final long userId) {
        final User user = userService.getUserById(userId).get();
        final HouseholdMember householdMember = user.getHouseholdMember();
        if (householdMember != null && householdMember.getHousehold().getOwner().equals(user)) {
            return new ResponseAPI<>(false, CreateHouseholdResponseMessage.USER_IS_ALREADY_OWNER);
        }

        if (householdMember != null) {
            return new ResponseAPI<>(false, CreateHouseholdResponseMessage.USER_IS_MEMBER);
        }

        final Household household = repository.save(new Household(createHouseholdRequest.name(), Instant.now(), user));

        final Role defaultRole = roleService.createDefaultRole(household);
        final Role ownerDefaultRole = roleService.createOwnerDefaultRole(household);

        rolePermissionService.addPermissionsToDefaultRole(defaultRole);
        rolePermissionService.addPermissionsToOwnerDefaultRole(ownerDefaultRole);

        household.setDefaultRole(defaultRole);
        householdMemberService.addToHousehold(new HouseholdMember(household, user, ownerDefaultRole));

        final EventLog<HouseholdActionType> eventLog = EventLogBuilder
                .<HouseholdActionType, Household>builder()
                .entityType(Household.class)
                .actionType(HouseholdActionType.CREATE)
                .description(createHouseholdRequest.name())
                .executorUser(user)
                .build().build();

        eventLogService.log(eventLog);

        return new ResponseAPI<>(true, CreateHouseholdResponseMessage.SUCCESS);
    }

    //TODO W PRZYSZLOSCI DODAC USUWANIE GOSPODARSTWA
}
