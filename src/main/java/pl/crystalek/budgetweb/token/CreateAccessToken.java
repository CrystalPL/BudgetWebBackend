package pl.crystalek.budgetweb.token;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.token.model.AccessTokenDetails;
import pl.crystalek.budgetweb.token.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class CreateAccessToken {
    TokenRepository tokenRepository;
    TokenCreator tokenCreator;

    public Optional<String> createAndGetAccessToken(final AccessTokenDetails tokenDetails) {
        final long userId = tokenDetails.getUserId();
        final long refreshTokenId = tokenDetails.getRefreshTokenId();

        final Optional<RefreshToken> refreshTokenOptional = tokenRepository.findById(refreshTokenId);
        if (refreshTokenOptional.isEmpty()) {
            return Optional.empty();
        }

        final RefreshToken refreshToken = refreshTokenOptional.get();
        if (Instant.now().isAfter(refreshToken.getExpireAt()) || refreshToken.getUser().getId() != userId) {
            return Optional.empty();
        }

        return Optional.of(tokenCreator.create(userId, refreshTokenId));
    }
}
