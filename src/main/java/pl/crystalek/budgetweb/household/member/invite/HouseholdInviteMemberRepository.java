package pl.crystalek.budgetweb.household.member.invite;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

interface HouseholdInviteMemberRepository extends CrudRepository<HouseholdInviteMember, UUID> {

    @Query("SELECT h FROM HouseholdInviteMember h WHERE h.household.id = " +
           "(SELECT hm.household.id FROM HouseholdMember hm WHERE hm.user.id = :userId)")
    Set<HouseholdInviteMember> getInvitedUsersByUserId(final long userId);

}
