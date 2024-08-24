package pl.crystalek.budgetweb.auth.confirmation;

import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface AccountConfirmationRepository extends CrudRepository<AccountConfirmation, UUID> {

    Optional<AccountConfirmation> findByUser_Email(final String email);

    void deleteAllByExpireAtBefore(final Instant now);
}
