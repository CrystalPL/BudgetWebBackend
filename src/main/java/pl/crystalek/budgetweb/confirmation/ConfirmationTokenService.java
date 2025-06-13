package pl.crystalek.budgetweb.confirmation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConfirmationTokenService {
    ConfirmationTokenRepository repository;

    public ConfirmationToken getToken(final User user, final Instant expireAt, final ConfirmationTokenType confirmationTokenType) {
        return repository.save(new ConfirmationToken(user, expireAt, confirmationTokenType));
    }

    public Optional<ConfirmationToken> getConfirmationToken(final String email, final ConfirmationTokenType confirmationTokenType) {
        return repository.findByUser_EmailAndConfirmationTokenType(email, confirmationTokenType);
    }

    public Optional<ConfirmationToken> getConfirmationToken(final UUID tokenId, final ConfirmationTokenType confirmationTokenType) {
        return repository.findByIdAndConfirmationTokenType(tokenId, confirmationTokenType);
    }

    public void delete(final ConfirmationToken confirmationToken) {
        repository.delete(confirmationToken);
    }

    public void clearByExpireTime(final ConfirmationTokenType confirmationTokenType) {
        repository.deleteAllByExpireAtBeforeAndConfirmationTokenType(Instant.now(), confirmationTokenType);
    }
}
