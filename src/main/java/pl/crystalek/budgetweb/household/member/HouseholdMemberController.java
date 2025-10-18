package pl.crystalek.budgetweb.household.member;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.member.response.DeleteMemberResponseMessage;
import pl.crystalek.budgetweb.household.member.response.GetMembersResponse;
import pl.crystalek.budgetweb.household.member.response.LeaveHouseholdResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseholdMemberController {
    HouseholdMemberService householdMemberService;

    @GetMapping("/getMembers")
    public Set<GetMembersResponse> getMembers(@AuthenticationPrincipal final long userId) {
        return householdMemberService.getMembersResponse(userId);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).HOUSEHOLD_MEMBER_DELETE)")
    @DeleteMapping("/deleteMember/{id}")
    public ResponseEntity<ResponseAPI<DeleteMemberResponseMessage>> deleteUser(
            @PathVariable("id") final String memberId,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<DeleteMemberResponseMessage> response = householdMemberService.deleteUser(memberId, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/leave")
    public ResponseEntity<ResponseAPI<LeaveHouseholdResponseMessage>> leaveHousehold(@AuthenticationPrincipal final long userId) {
        final ResponseAPI<LeaveHouseholdResponseMessage> response = householdMemberService.leaveHousehold(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
