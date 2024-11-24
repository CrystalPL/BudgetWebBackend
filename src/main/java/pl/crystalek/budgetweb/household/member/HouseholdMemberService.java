package pl.crystalek.budgetweb.household.member;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HouseholdMemberService {
    HouseholdMemberRepository repository;

    public void addToHousehold(final HouseholdMember householdMember) {
        repository.save(householdMember);
    }
}
