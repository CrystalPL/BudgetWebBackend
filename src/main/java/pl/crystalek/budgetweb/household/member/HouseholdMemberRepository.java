package pl.crystalek.budgetweb.household.member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.household.member.model.GetMembersResponse;

import java.util.Optional;
import java.util.Set;

interface HouseholdMemberRepository extends CrudRepository<HouseholdMember, Long> {

    Optional<HouseholdMember> findByUser_Id(final long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.household.member.model.GetMembersResponse(u.id, u.nickname,
                        new pl.crystalek.budgetweb.household.member.model.HouseholdMemberRoleDTO(r.name, r.color))
            FROM HouseholdMember hm
                     JOIN User u on u.id = hm.user.id
                     JOIN Role r ON hm.role.id = r.id
            WHERE u.id = :userId""")
    Set<GetMembersResponse> findAllHouseholdMembersByUserId(final long userId);
}
