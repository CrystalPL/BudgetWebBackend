package pl.crystalek.budgetweb.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class TokenCreator {
    TokenProperties tokenProperties;

    String create(final long userId, final long refreshTokenId) {
        return createWithExpires(userId, refreshTokenId, Instant.now().plus(tokenProperties.getTokenAccessTime()));
    }

    private String createWithExpires(final long userId, final long refreshTokenId, final Instant expiresAt) {
        return JWT.create()
                .withExpiresAt(expiresAt)
                .withClaim("refreshTokenId", refreshTokenId)
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC256(tokenProperties.getSecretKey()));
    }
}
