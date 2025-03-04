package pl.crystalek.budgetweb.household.member;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.member.model.GetMembersResponse;

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
        return repository.findAllHouseholdMembersByUserId(userId);
    }
}
