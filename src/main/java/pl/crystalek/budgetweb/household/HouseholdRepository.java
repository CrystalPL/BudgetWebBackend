package pl.crystalek.budgetweb.household;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface HouseholdRepository extends CrudRepository<Household, Long> {

    @Modifying
    @Query("""
            UPDATE Household h SET h.name = :newHouseholdName WHERE h.id =
                        (SELECT hm.household.id FROM HouseholdMember hm WHERE hm.user.id = :requesterId)""")
    void updateHouseholdName(final long requesterId, final String newHouseholdName);

    Household findByMembers_User_Id(final long userId);

    boolean existsByMembers_User_Id(final long userId);
}
