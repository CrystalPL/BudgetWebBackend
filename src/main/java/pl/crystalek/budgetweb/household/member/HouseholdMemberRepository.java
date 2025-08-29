package pl.crystalek.budgetweb.household.member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.household.member.response.GetMembersResponse;

import java.util.Optional;
import java.util.Set;

interface HouseholdMemberRepository extends CrudRepository<HouseholdMember, Long> {

    Optional<HouseholdMember> findByUser_Id(final long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.household.member.response.GetMembersResponse(u.id, u.userData.nickname,
                        new pl.crystalek.budgetweb.household.member.response.HouseholdMemberRoleDTO(r.name, r.color))
            FROM HouseholdMember hm
                     JOIN User u on u.id = hm.user.id
                     JOIN Role r ON hm.role.id = r.id
                     JOIN HouseholdMember myMember ON myMember.household.id = hm.household.id
            WHERE myMember.user.id = :userId""")
    Set<GetMembersResponse> findAllHouseholdMembersByUserId(final long userId);

    @Query("""
            SELECT CASE WHEN COUNT(hm1) > 0 THEN true ELSE false END
            FROM HouseholdMember hm1,
                 HouseholdMember hm2
            WHERE hm1.user.id = :userId1
              AND hm2.user.id = :userId2
              AND hm1.household.id = hm2.household.id
            """)
    boolean isSameHousehold(final long userId1, final long userId2);
}
