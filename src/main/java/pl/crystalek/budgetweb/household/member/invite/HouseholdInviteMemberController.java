package pl.crystalek.budgetweb.household.member.invite;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.member.invite.request.InviteHouseholdMemberRequest;
import pl.crystalek.budgetweb.household.member.invite.request.UndoInvitationRequest;
import pl.crystalek.budgetweb.household.member.invite.response.GetInvitedUsersResponse;
import pl.crystalek.budgetweb.household.member.invite.response.InviteHouseholdMemberResponseMessage;
import pl.crystalek.budgetweb.household.member.invite.response.UndoInvitationResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/household/invitations")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseholdInviteMemberController {
    HouseholdInviteMemberService invitationsMembersService;

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).HOUSEHOLD_MEMBER_INVITE)")
    @PostMapping("/invite")
    public ResponseEntity<ResponseAPI<InviteHouseholdMemberResponseMessage>> inviteMember(
            @RequestBody @Valid final InviteHouseholdMemberRequest inviteHouseholdMemberRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<InviteHouseholdMemberResponseMessage> response = invitationsMembersService.invite(inviteHouseholdMemberRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).HOUSEHOLD_MEMBER_CANCEL_INVITATION)")
    @PostMapping("/undo")
    public ResponseEntity<ResponseAPI<UndoInvitationResponseMessage>> undoInvitation(
            @RequestBody @Valid final UndoInvitationRequest undoInvitationRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<UndoInvitationResponseMessage> response = invitationsMembersService.undoInvitation(undoInvitationRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getInvitedUsers")
    public Set<GetInvitedUsersResponse> getInvitedUsers(@AuthenticationPrincipal final long userId) {
        return invitationsMembersService.getInvitedUsers(userId);
    }
}
