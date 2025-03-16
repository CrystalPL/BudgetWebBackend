package pl.crystalek.budgetweb.household.role;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.household.role.model.RoleResponse;

import java.util.Set;

interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("""
            SELECT new pl.crystalek.budgetweb.household.role.model.RoleResponse(r.id, r.name)
            FROM Role r
                     JOIN HouseholdMember hm ON hm.user.id = :userId
                     JOIN Household h ON hm.household.id = h.id
            WHERE r.household.id = hm.household.id AND h.ownerRole.id != r.id
            """)
    Set<RoleResponse> getRoles(final long userId);

    @Modifying
    @Query("UPDATE HouseholdMember hm SET hm.role.id = :roleId WHERE hm.user.id = :userId")
    void editUserRole(final long roleId, final long userId);
}
