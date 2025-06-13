package pl.crystalek.budgetweb.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.crystalek.budgetweb.auth.token.model.AccessTokenDetails;

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
    @Mock(lenient = true)
    TokenProperties tokenProperties;
    TokenDecoder tokenDecoder;
    TokenCreator tokenCreator;

    @BeforeEach
    void setUp() {
        when(tokenProperties.getSecretKey()).thenReturn(secretKey);
        when(tokenProperties.getTokenAccessTime()).thenReturn(Duration.ofMinutes(15));

        tokenDecoder = new TokenDecoder(tokenProperties);
        tokenCreator = new TokenCreator(tokenProperties);
    }

    @Test
    void shouldDecodeValidToken() {
        // given
        final long userId = 123L;
        final long refreshTokenId = 456L;
        final Instant expiresAt = Instant.now().plus(Duration.ofHours(1));
        final String token = tokenCreator.createWithExpires(userId, refreshTokenId, expiresAt);

        // when
        final AccessTokenDetails result = tokenDecoder.decodeToken(token);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(refreshTokenId, result.getRefreshTokenId());
        assertEquals(expiresAt.getEpochSecond(), result.getExpiresAt().getEpochSecond());
        assertTrue(result.isVerified());
    }

    @Test
    void shouldDecodeExpiredToken() {
        // given
        final long userId = 123L;
        final long refreshTokenId = 456L;
        final Instant expiresAt = Instant.now().minus(Duration.ofMinutes(5));
        final String token = tokenCreator.createWithExpires(userId, refreshTokenId, expiresAt);

        // when
        final AccessTokenDetails result = tokenDecoder.decodeToken(token);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.isExpired());
        assertEquals(refreshTokenId, result.getRefreshTokenId());
        assertEquals(expiresAt.getEpochSecond(), result.getExpiresAt().getEpochSecond());
    }

    @Test
    void shouldHandleInvalidToken() {
        // given
        final String invalidToken = "invalid.token.structure";

        // when
        final AccessTokenDetails result = tokenDecoder.decodeToken(invalidToken);

        // then
        assertNotNull(result);
        assertFalse(result.isVerified());
    }

    @Test
    void shouldHandleTokenWithInvalidSignature() {
        // given
        final long userId = 123L;
        final long refreshTokenId = 456L;

        // Tworzymy token z innym kluczem niż ten używany do weryfikacji
        final String tamperedToken = JWT.create()
                .withExpiresAt(Instant.now().plus(Duration.ofHours(1)))
                .withClaim("refreshTokenId", refreshTokenId)
                .withClaim("userId", userId)
                .sign(Algorithm.HMAC256("differentSecretKey"));

        // when
        final AccessTokenDetails result = tokenDecoder.decodeToken(tamperedToken);

        // then
        assertNotNull(result);
        assertFalse(result.isVerified());
    }
}