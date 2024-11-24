package pl.crystalek.budgetweb.household.member.invite;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.member.invite.model.InviteHouseholdMemberRequest;
import pl.crystalek.budgetweb.household.member.invite.model.InviteHouseholdMemberResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

@RestController
@RequestMapping("/household/members")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseholdInviteMemberController {
    HouseholdInviteMemberService householdInviteMemberService;

    @PostMapping("/invite")
    private ResponseEntity<ResponseAPI<InviteHouseholdMemberResponseMessage>> inviteMember(@RequestBody @Valid InviteHouseholdMemberRequest inviteHouseholdMemberRequest) {
        final ResponseAPI<InviteHouseholdMemberResponseMessage> response = householdInviteMemberService.invite(inviteHouseholdMemberRequest, 1);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
