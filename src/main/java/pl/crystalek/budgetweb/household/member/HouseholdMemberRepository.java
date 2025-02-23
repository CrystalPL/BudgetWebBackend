package pl.crystalek.budgetweb.household.member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

interface HouseholdMemberRepository extends CrudRepository<HouseholdMember, Long> {

    boolean existsByUser_Id(final long userId);

    @Query("SELECT hm FROM HouseholdMember hm WHERE hm.household.id = (" +
           "SELECT h.household.id FROM HouseholdMember h WHERE h.user.id = :userId)")
    Set<HouseholdMember> findAllHouseholdMembersByUserId(final long userId);
}
