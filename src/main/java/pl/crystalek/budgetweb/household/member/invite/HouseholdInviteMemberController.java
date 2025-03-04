package pl.crystalek.budgetweb.household.member.invite;

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
import pl.crystalek.budgetweb.household.member.invite.model.GetInvitedUsersResponse;
import pl.crystalek.budgetweb.household.member.invite.model.InviteHouseholdMemberRequest;
import pl.crystalek.budgetweb.household.member.invite.model.InviteHouseholdMemberResponseMessage;
import pl.crystalek.budgetweb.household.member.invite.model.UndoInvitationRequest;
import pl.crystalek.budgetweb.household.member.invite.model.UndoInvitationResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/household/invitations")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseholdInviteMemberController {
    HouseholdInviteMemberService invitationsMembersService;

    @PostMapping("/invite")
    private ResponseEntity<ResponseAPI<InviteHouseholdMemberResponseMessage>> inviteMember(@RequestBody @Valid final InviteHouseholdMemberRequest inviteHouseholdMemberRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final ResponseAPI<InviteHouseholdMemberResponseMessage> response = invitationsMembersService.invite(inviteHouseholdMemberRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/undo")
    private ResponseEntity<ResponseAPI<UndoInvitationResponseMessage>> undoInvitation(@RequestBody @Valid final UndoInvitationRequest undoInvitationRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final ResponseAPI<UndoInvitationResponseMessage> response = invitationsMembersService.undoInvitation(undoInvitationRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getInvitedUsers")
    private Set<GetInvitedUsersResponse> getInvitedUsers() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return invitationsMembersService.getInvitedUsers(userId);
    }
}
