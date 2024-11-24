package pl.crystalek.budgetweb.household.member.invite;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.invite.model.InviteHouseholdMemberRequest;
import pl.crystalek.budgetweb.household.member.invite.model.InviteHouseholdMemberResponseMessage;
import pl.crystalek.budgetweb.log.EventLog;
import pl.crystalek.budgetweb.log.EventLogBuilder;
import pl.crystalek.budgetweb.log.EventLogService;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.User;
import pl.crystalek.budgetweb.user.UserService;

import java.time.Instant;
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HouseholdInviteMemberService {
    HouseholdInviteMemberRepository repository;
    UserService userService;
    EventLogService eventLogService;

    public ResponseAPI<InviteHouseholdMemberResponseMessage> invite(final InviteHouseholdMemberRequest inviteHouseholdMemberRequest, final long requesterUserId) {
        final Optional<User> userOptional = userService.getUserByEmail(inviteHouseholdMemberRequest.email());
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, InviteHouseholdMemberResponseMessage.USER_NOT_FOUND);
        }

        final User invitedUser = userOptional.get();
        if (invitedUser.getHouseholdMember() != null) {
            return new ResponseAPI<>(false, InviteHouseholdMemberResponseMessage.USER_ALREADY_HAS_HOUSEHOLD);
        }

        final User requesterUser = userService.getUserById(requesterUserId).get();
        final Household household = requesterUser.getHouseholdMember().getHousehold();
        if (household == null) {
            return new ResponseAPI<>(false, InviteHouseholdMemberResponseMessage.EMPTY_HOUSEHOLD);
        }

        final HouseholdInviteMember householdInviteMember = new HouseholdInviteMember(household, invitedUser, Instant.now());

        try {
            repository.save(householdInviteMember);
        } catch (final DataIntegrityViolationException exception) {
            return new ResponseAPI<>(false, InviteHouseholdMemberResponseMessage.USER_ALREADY_INVITE);
        }

        final EventLog<HouseholdInviteMemberActionType> eventLog = EventLogBuilder
                .<HouseholdInviteMemberActionType, HouseholdInviteMember>builder()
                .entityType(HouseholdInviteMember.class)
                .actionType(HouseholdInviteMemberActionType.INVITE)
                .description(invitedUser.getNickname())
                .executorUser(requesterUser)
                .build().build();

        eventLogService.log(eventLog);

        return new ResponseAPI<>(true, InviteHouseholdMemberResponseMessage.SUCCESS);
    }
}
