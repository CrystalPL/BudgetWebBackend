package pl.crystalek.budgetweb.household.role.permission;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

interface RolePermissionRepository extends CrudRepository<RolePermission, Long> {

    @Query("SELECT rp.permissionName " +
           "FROM HouseholdMember hm " +
           "JOIN hm.role r " +
           "JOIN r.permissionSet rp " +
           "WHERE hm.user.id = :userId")
    Set<Permission> findPermissionNamesByUserId(@Param("userId") final Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permissionName NOT IN :permissionNames")
    void deletePermissions(final long roleId, final Collection<Permission> permissionNames);
}
