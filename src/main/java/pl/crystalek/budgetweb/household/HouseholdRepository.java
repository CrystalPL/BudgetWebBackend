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

    @Query("""
            SELECT h
            FROM Household h
                     JOIN HouseholdMember hm ON hm.household.id = h.id
            WHERE hm.user.id = :userId
            """)
    Household getHouseholdByUserId(final long userId);
}
