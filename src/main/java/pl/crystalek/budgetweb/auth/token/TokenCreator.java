package pl.crystalek.budgetweb.auth.token;

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
public class TokenCreator {
    TokenProperties tokenProperties;

    public String createWithExpires(final long userId, final long refreshTokenId, final Instant expiresAt) {
        return JWT.create()
                .withExpiresAt(expiresAt)
                .withClaim("refreshTokenId", refreshTokenId)
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC256(tokenProperties.getSecretKey()));
    }

    public String create(final long userId, final long refreshTokenId) {
        return createWithExpires(userId, refreshTokenId, Instant.now().plus(tokenProperties.getTokenAccessTime()));
    }
}
