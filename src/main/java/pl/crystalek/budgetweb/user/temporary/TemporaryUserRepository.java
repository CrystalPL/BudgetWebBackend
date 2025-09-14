package pl.crystalek.budgetweb.user.temporary;

import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface TemporaryUserRepository extends CrudRepository<TemporaryUser, UUID> {

    boolean existsByEmail(final String email);

    Optional<TemporaryUser> findByEmail(final String email);

    void deleteAllByExpireAtBefore(Instant expireAtBefore);
}
