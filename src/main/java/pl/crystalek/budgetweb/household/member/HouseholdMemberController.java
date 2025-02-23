package pl.crystalek.budgetweb.household.member;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.member.model.GetMembersResponse;

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
}
