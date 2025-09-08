package pl.crystalek.budgetweb.user.avatar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.crystalek.budgetweb.user.avatar.response.AvatarWithHouseholdCheckResponse;

import java.util.Optional;
import java.util.UUID;

@Repository
interface AvatarRepository extends CrudRepository<Avatar, UUID> {

    Optional<Avatar> findByUser_Id(final long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.user.avatar.response.AvatarWithHouseholdCheckResponse(
                a,
                CASE WHEN COUNT(hm2) > 0 THEN true ELSE false END
            )
            FROM User u
            LEFT JOIN Avatar a ON a.user.id = u.id
            LEFT JOIN HouseholdMember hm1 ON hm1.user.id = u.id
            LEFT JOIN HouseholdMember hm2 ON hm2.user.id = :requesterUserId AND hm1.household.id = hm2.household.id
            WHERE u.id = :targetUserId
            """)
    AvatarWithHouseholdCheckResponse findAvatarWithHouseholdCheck(final long targetUserId, final long requesterUserId);
}
