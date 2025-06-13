package pl.crystalek.budgetweb.household.role;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.household.role.response.ChangeRoleResponse;
import pl.crystalek.budgetweb.household.role.response.RoleListResponse;

import java.util.Set;

interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("""
            SELECT new pl.crystalek.budgetweb.household.role.response.ChangeRoleResponse(r.id, r.name)
            FROM Role r
                     JOIN HouseholdMember hm ON hm.user.id = :userId
                     JOIN Household h ON hm.household.id = h.id
            WHERE r.household.id = hm.household.id
            """)
    Set<ChangeRoleResponse> getChangeRoleResponse(final long userId);

    @Modifying
    @Query("UPDATE HouseholdMember hm SET hm.role.id = :roleId WHERE hm.user.id = :userId")
    void changeUserRole(final long roleId, final long userId);

    @Modifying
    @Query("UPDATE Household h SET h.defaultRole.id = :roleId WHERE h.id = :householdId")
    void editHouseholdDefaultRole(final long roleId, final long householdId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.household.role.response.RoleListResponse(
                r.id,
                r.name,
                r.color,
                CASE WHEN h.defaultRole.id = r.id THEN true ELSE false END,
                CASE WHEN h.ownerRole.id = r.id THEN true ELSE false END
            )
            FROM Role r
                JOIN HouseholdMember hm ON hm.user.id = :userId
                JOIN Household h ON hm.household.id = h.id
            WHERE r.household.id = hm.household.id
            """)
    Set<RoleListResponse> getRoleListResponse(final long userId);
}
