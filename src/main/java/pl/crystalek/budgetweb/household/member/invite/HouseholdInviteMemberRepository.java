package pl.crystalek.budgetweb.household.member.invite;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.household.member.invite.response.GetInvitedUsersResponse;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

interface HouseholdInviteMemberRepository extends CrudRepository<HouseholdInviteMember, UUID> {

    @Query("""
            SELECT new pl.crystalek.budgetweb.household.member.invite.response.GetInvitedUsersResponse(u.id, u.email, him.inviteDate)
            FROM HouseholdInviteMember him
                     JOIN HouseholdMember hm ON hm.user.id = :userId
                     JOIN User u ON him.user.id = u.id
            WHERE him.household.id = hm.household.id""")
    Set<GetInvitedUsersResponse> getInvitedUsersByUserId(final long userId);

    @Query("""
            SELECT him
            FROM HouseholdInviteMember him
            WHERE him.user.id = :userId
              AND him.household.id = (SELECT hm.household.id FROM HouseholdMember hm WHERE hm.user.id = :requestId)
            """)
    Optional<HouseholdInviteMember> selectMember(final long userId, final long requestId);

}