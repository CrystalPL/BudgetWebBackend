package pl.crystalek.budgetweb.household.member;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.member.model.GetMembersResponse;
import pl.crystalek.budgetweb.user.User;

import java.util.HashSet;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HouseholdMemberService {
    HouseholdMemberRepository repository;

    public void addToHousehold(final HouseholdMember householdMember) {
        repository.save(householdMember);
    }

    public Set<GetMembersResponse> getMembersResponse(final long userId) {
        final Set<HouseholdMember> members = repository.findAllHouseholdMembersByUserId(userId);

        final Set<GetMembersResponse> response = new HashSet<>();
        for (final HouseholdMember member : members) {
            final User user = member.getUser();
            response.add(new GetMembersResponse(user.getId(), user.getNickname(), member.getRole().getName()));
        }

        return response;
    }
}
