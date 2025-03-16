package pl.crystalek.budgetweb.household.member;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.member.model.DeleteMemberResponseMessage;
import pl.crystalek.budgetweb.household.member.model.GetMembersResponse;
import pl.crystalek.budgetweb.household.member.model.LeaveHouseholdResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseholdMemberController {
    HouseholdMemberService householdMemberService;

    @GetMapping("/getMembers")
    private Set<GetMembersResponse> getMembers() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return householdMemberService.getMembersResponse(userId);
    }

    @DeleteMapping("/deleteMember/{id}")
    private ResponseEntity<ResponseAPI<DeleteMemberResponseMessage>> deleteUser(@PathVariable("id") final String memberId) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<DeleteMemberResponseMessage> response = householdMemberService.deleteUser(memberId, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/leave")
    private ResponseEntity<ResponseAPI<LeaveHouseholdResponseMessage>> leaveHousehold() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<LeaveHouseholdResponseMessage> response = householdMemberService.leaveHousehold(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
