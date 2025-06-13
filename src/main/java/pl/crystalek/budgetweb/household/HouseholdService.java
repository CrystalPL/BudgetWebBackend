package pl.crystalek.budgetweb.household;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.household.member.HouseholdMemberService;
import pl.crystalek.budgetweb.household.request.CreateHouseholdRequest;
import pl.crystalek.budgetweb.household.response.ChangeHouseholdNameResponseMessage;
import pl.crystalek.budgetweb.household.response.CreateHouseholdResponseMessage;
import pl.crystalek.budgetweb.household.response.DeleteHouseholdResponseMessage;
import pl.crystalek.budgetweb.household.response.TransferOwnerResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HouseholdService {
    HouseholdRepository repository;
    UserService userService;
    HouseholdMemberService householdMemberService;
    ApplicationEventPublisher eventPublisher;
    CacheManager cacheManager;

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
        eventPublisher.publishEvent(new CreateHouseholdEvent(household));

//        final EventLog<HouseholdActionType> eventLog = EventLogBuilder
//                .<HouseholdActionType, Household>builder()
//                .entityType(Household.class)
//                .actionType(HouseholdActionType.CREATE)
//                .description(createHouseholdRequest.productName())
//                .executorUser(user)
//                .build().build();
//
//        eventLogService.log(eventLog);

        return new ResponseAPI<>(true, CreateHouseholdResponseMessage.SUCCESS);
    }

    @Transactional
    public ResponseAPI<ChangeHouseholdNameResponseMessage> changeHouseholdName(final String newHouseholdName, final long requesterId) {
        repository.updateHouseholdName(requesterId, newHouseholdName);

        return new ResponseAPI<>(true, ChangeHouseholdNameResponseMessage.SUCCESS);
    }

    public ResponseAPI<DeleteHouseholdResponseMessage> deleteHousehold(final long requesterId) {
        final Optional<HouseholdMember> householdMemberOptional = householdMemberService.getHouseholdMemberByUserId(requesterId);
        if (householdMemberOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteHouseholdResponseMessage.HOUSEHOLD_NOT_FOUND);
        }

        final Household household = householdMemberOptional.get().getHousehold();
        repository.delete(household);

        return new ResponseAPI<>(true, DeleteHouseholdResponseMessage.SUCCESS);
    }

    public ResponseAPI<TransferOwnerResponseMessage> transferOwner(final long memberId, final long requesterId) {
        if (memberId == requesterId) {
            return new ResponseAPI<>(false, TransferOwnerResponseMessage.YOURSELF_TRANSFER);
        }

        final Household household = repository.getHouseholdByUserId(requesterId);
        final User oldOwner = household.getOwner();
        if (oldOwner.getId() == requesterId) {
            return new ResponseAPI<>(false, TransferOwnerResponseMessage.NO_OWNER);
        }

        final Optional<HouseholdMember> householdMemberOptional = household.getMembers().stream()
                .filter(householdMember -> householdMember.getUser().getId() == memberId)
                .findFirst();

        if (householdMemberOptional.isEmpty()) {
            return new ResponseAPI<>(false, TransferOwnerResponseMessage.USER_NOT_FOUND);
        }

        final HouseholdMember householdMember = householdMemberOptional.get();
        oldOwner.getHouseholdMember().setRole(household.getDefaultRole());
        household.setOwner(householdMember.getUser());
        householdMember.setRole(household.getOwnerRole());

        return new ResponseAPI<>(true, TransferOwnerResponseMessage.SUCCESS);
    }
}
