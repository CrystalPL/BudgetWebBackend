package pl.crystalek.budgetweb.household.member;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.household.CreateHouseholdEvent;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.response.DeleteMemberResponseMessage;
import pl.crystalek.budgetweb.household.member.response.GetMembersResponse;
import pl.crystalek.budgetweb.household.member.response.LeaveHouseholdResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HouseholdMemberService {
    HouseholdMemberRepository repository;

    @EventListener
    @Order(3)
    public void createOwnerObjectWhenHouseholdIsCreating(final CreateHouseholdEvent event) {
        final Household household = event.household();
        final HouseholdMember householdMember = new HouseholdMember(household, household.getOwner(), household.getOwnerRole(), Instant.now());
        repository.save(householdMember);
    }

    public Set<GetMembersResponse> getMembersResponse(final long userId) {
        return repository.findAllHouseholdMembersByUserId(userId);
    }

    public Optional<HouseholdMember> getHouseholdMemberByUserId(final long userId) {
        return repository.findByUser_Id(userId);
    }

    public ResponseAPI<DeleteMemberResponseMessage> deleteUser(final String stringMemberId, final long requesterId) {
        if (stringMemberId == null || stringMemberId.isBlank()) {
            return new ResponseAPI<>(false, DeleteMemberResponseMessage.MISSING_MEMBER_ID);
        }

        final long memberId;
        try {
            memberId = Long.parseLong(stringMemberId);
        } catch (final NumberFormatException exception) {
            return new ResponseAPI<>(false, DeleteMemberResponseMessage.ERROR_NUMBER_FORMAT);
        }

        if (memberId == requesterId) {
            return new ResponseAPI<>(false, DeleteMemberResponseMessage.YOURSELF_DELETE);
        }

        final Optional<HouseholdMember> householdMemberOptional = getHouseholdMemberByUserId(memberId);
        if (householdMemberOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteMemberResponseMessage.MEMBER_NOT_FOUND);
        }

        final HouseholdMember householdMember = householdMemberOptional.get();
        final Household household = householdMember.getHousehold();

        if (household.getOwner().getId() == memberId) {
            return new ResponseAPI<>(false, DeleteMemberResponseMessage.USER_IS_OWNER);
        }

        final boolean sameHousehold = household.getMembers().stream().anyMatch(member -> member.getUser().getId() == requesterId);
        if (!sameHousehold) {
            return new ResponseAPI<>(false, DeleteMemberResponseMessage.DIFFERENT_HOUSEHOLD);
        }

        repository.delete(householdMember);
        return new ResponseAPI<>(true, DeleteMemberResponseMessage.SUCCESS);
    }

    @Transactional
    public ResponseAPI<LeaveHouseholdResponseMessage> leaveHousehold(final long userId) {
        final Optional<HouseholdMember> householdMemberOptional = getHouseholdMemberByUserId(userId);
        if (householdMemberOptional.isEmpty()) {
            return new ResponseAPI<>(false, LeaveHouseholdResponseMessage.HOUSEHOLD_NOT_FOUND);
        }

        final HouseholdMember householdMember = householdMemberOptional.get();
        final Household household = householdMember.getHousehold();
        final Set<HouseholdMember> members = household.getMembers();
        if (members.size() == 1) {
            return new ResponseAPI<>(false, LeaveHouseholdResponseMessage.ONE_MEMBER_IN_HOUSEHOLD);
        }

        //gdyby to opuścić gospodarstwo chciał właściciel
        if (household.getOwner().getId() == userId) {
            //Ignoruje optionala ponieważ, members nigdy nie będą mniejsze od jeden,
            // a gdy będą równe jeden to wykona się powyższy warunek, zatem members >= 2
            final HouseholdMember longestStandingMember = members.stream()
                    .min(Comparator.comparing(HouseholdMember::getJoinDate))
                    .get();

            household.setOwner(longestStandingMember.getUser());
        }

        repository.delete(householdMember);

        return new ResponseAPI<>(true, LeaveHouseholdResponseMessage.SUCCESS);
    }
}
