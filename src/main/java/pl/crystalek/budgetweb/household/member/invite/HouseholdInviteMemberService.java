package pl.crystalek.budgetweb.household.member.invite;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.invite.request.InviteHouseholdMemberRequest;
import pl.crystalek.budgetweb.household.member.invite.request.UndoInvitationRequest;
import pl.crystalek.budgetweb.household.member.invite.response.GetInvitedUsersResponse;
import pl.crystalek.budgetweb.household.member.invite.response.InviteHouseholdMemberResponseMessage;
import pl.crystalek.budgetweb.household.member.invite.response.UndoInvitationResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HouseholdInviteMemberService {
    HouseholdInviteMemberRepository repository;
    UserService userService;

    public Set<GetInvitedUsersResponse> getInvitedUsers(final long userId) {
        return repository.getInvitedUsersByUserId(userId);
    }

    public ResponseAPI<UndoInvitationResponseMessage> undoInvitation(final UndoInvitationRequest undoInvitationRequest, final long requesterUserId) {
        final Optional<HouseholdInviteMember> householdInviteMemberOptional = repository.selectMember(undoInvitationRequest.getMemberId(), requesterUserId);
        if (householdInviteMemberOptional.isEmpty()) {
            return new ResponseAPI<>(false, UndoInvitationResponseMessage.INVITATION_NOT_FOUND);
        }

        final HouseholdInviteMember householdInviteMember = householdInviteMemberOptional.get();
        repository.delete(householdInviteMember);
//        final UserDTO userDTO = userService.getUserDTO(requesterUserId).get();
//
//        final EventLog<HouseholdInviteMemberActionType> eventLog = EventLogBuilder
//                .<HouseholdInviteMemberActionType, HouseholdInviteMember>builder()
//                .entityType(HouseholdInviteMember.class)
//                .actionType(HouseholdInviteMemberActionType.UNDO)
//                .description(userDTO. ())
//                .household(householdInviteMember.getHousehold())
//                .executorUser("requesterUserId")
//                .build().build();
//
//        eventLogService.log(eventLog);

        return new ResponseAPI<>(true, UndoInvitationResponseMessage.SUCCESS);
    }

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

//        final EventLog<HouseholdInviteMemberActionType> eventLog = EventLogBuilder
//                .<HouseholdInviteMemberActionType, HouseholdInviteMember>builder()
//                .entityType(HouseholdInviteMember.class)
//                .actionType(HouseholdInviteMemberActionType.INVITE)
//                .description(invitedUser.getNickname())
//                .executorUser(requesterUser)
//                .household(household)
//                .build().build();
//
//        eventLogService.log(eventLog);

        return new ResponseAPI<>(true, InviteHouseholdMemberResponseMessage.SUCCESS);
    }
}
