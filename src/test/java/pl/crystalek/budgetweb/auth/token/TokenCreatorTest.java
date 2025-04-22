package pl.crystalek.budgetweb.auth.token;

import com.auth0.jwt.JWT;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class TokenCreatorTest {
    @Mock(lenient = true)
    TokenProperties tokenProperties;
    TokenCreator tokenCreator;

    @BeforeEach
    void setUp() {
        tokenCreator = new TokenCreator(tokenProperties);
        when(tokenProperties.getSecretKey()).thenReturn("testSecretKey123456789012345678901234567890");
        when(tokenProperties.getTokenAccessTime()).thenReturn(Duration.ofMinutes(15));
    }

    @Test
    void shouldCreateTokenWithSpecifiedExpiration() {
        // given
        final long userId = 123L;
        final long refreshTokenId = 456L;
        final Instant expiresAt = Instant.now().plus(Duration.ofHours(1));

        // when
        final String token = tokenCreator.createWithExpires(userId, refreshTokenId, expiresAt);

        // then
        assertThat(token).isNotEmpty();

        final var decodedJWT = JWT.decode(token);
        assertThat(decodedJWT.getClaim("userId").asLong()).isEqualTo(userId);
        assertThat(decodedJWT.getClaim("refreshTokenId").asLong()).isEqualTo(refreshTokenId);
        assertThat(decodedJWT.getExpiresAtAsInstant().getEpochSecond()).isEqualTo(expiresAt.getEpochSecond());
    }

    @Test
    void shouldCreateTokenWithDefaultExpiration() {
        // given
        final long userId = 123L;
        final long refreshTokenId = 456L;
        final Instant beforeCreation = Instant.now();

        // when
        final String token = tokenCreator.create(userId, refreshTokenId);

        // then
        assertThat(token).isNotEmpty();

        final var decodedJWT = JWT.decode(token);
        assertThat(decodedJWT.getClaim("userId").asLong()).isEqualTo(userId);
        assertThat(decodedJWT.getClaim("refreshTokenId").asLong()).isEqualTo(refreshTokenId);

        assertThat(decodedJWT.getExpiresAtAsInstant()).isBetween(
                beforeCreation,
                beforeCreation.plus(Duration.ofMinutes(16))
        );
    }
}