package pl.crystalek.budgetweb.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.crystalek.budgetweb.token.model.AccessTokenDetails;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class TokenDecoderTest {
    final String secretKey = "testSecretKey123456789012345678901234567890";

    @Mock TokenProperties tokenProperties;
    @InjectMocks TokenDecoder tokenDecoder;
    @InjectMocks TokenCreator tokenCreator;

    @Test
    void shouldDecodeValidToken() {
        when(tokenProperties.getSecretKey()).thenReturn(secretKey);
        when(tokenProperties.getTokenAccessTime()).thenReturn(Duration.ofMinutes(150));

        final long userId = 123L;
        final long refreshTokenId = 456L;
        final Instant expiresAt = Instant.now().plus(Duration.ofHours(1));
        final String token = tokenCreator.create(userId, refreshTokenId);

        final AccessTokenDetails result = tokenDecoder.decodeToken(token);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(refreshTokenId, result.getRefreshTokenId());
        assertTrue(expiresAt.isBefore(result.getExpiresAt()));
        assertTrue(result.isVerified());
    }

    @Test
    void shouldDecodeExpiredToken() {
        when(tokenProperties.getSecretKey()).thenReturn(secretKey);
        when(tokenProperties.getTokenAccessTime()).thenReturn(Duration.ofMinutes(150));

        final long userId = 123L;
        final long refreshTokenId = 456L;
        final Instant expiresAt = Instant.now().minus(Duration.ofMinutes(5));
        final String token = tokenCreator.create(userId, refreshTokenId);

        final AccessTokenDetails result = tokenDecoder.decodeToken(token);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(refreshTokenId, result.getRefreshTokenId());
        assertTrue(expiresAt.isBefore(result.getExpiresAt()));
    }

    @Test
    void shouldHandleInvalidToken() {
        final String invalidToken = "invalid.token.structure";

        final AccessTokenDetails result = tokenDecoder.decodeToken(invalidToken);

        assertNotNull(result);
        assertFalse(result.isVerified());
    }

    @Test
    void shouldHandleTokenWithInvalidSignature() {
        when(tokenProperties.getSecretKey()).thenReturn(secretKey);
        final long userId = 123L;
        final long refreshTokenId = 456L;

        // Tworzymy token z innym kluczem niż ten używany do weryfikacji
        final String tamperedToken = JWT.create()
                .withExpiresAt(Instant.now().plus(Duration.ofHours(1)))
                .withClaim("refreshTokenId", refreshTokenId)
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC256("differentSecretKey"));

        final AccessTokenDetails result = tokenDecoder.decodeToken(tamperedToken);

        assertNotNull(result);
        assertFalse(result.isVerified());
    }
}