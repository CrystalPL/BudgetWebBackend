package pl.crystalek.budgetweb.confirmation;

import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, UUID> {

    Optional<ConfirmationToken> findByUser_EmailAndConfirmationTokenType(final String email, final ConfirmationTokenType confirmationTokenType);

    Optional<ConfirmationToken> findByIdAndConfirmationTokenType(final UUID tokenId, final ConfirmationTokenType confirmationTokenType);

    void deleteAllByExpireAtBeforeAndConfirmationTokenType(final Instant now, final ConfirmationTokenType confirmationTokenType);
}
