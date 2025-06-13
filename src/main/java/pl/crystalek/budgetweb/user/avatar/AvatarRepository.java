package pl.crystalek.budgetweb.user.avatar;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface AvatarRepository extends CrudRepository<Avatar, UUID> {

    Optional<Avatar> findByUser_Id(final long userId);
}
