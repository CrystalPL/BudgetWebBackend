package pl.crystalek.budgetweb.auth.passwordrecovery;

import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface PasswordRecoveryRepository extends CrudRepository<PasswordRecovery, UUID> {

    void deleteAllByExpireAtBefore(final Instant now);

    Optional<PasswordRecovery> findByUser_Email(final String email);
}
