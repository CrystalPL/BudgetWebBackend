package pl.crystalek.budgetweb.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.token.model.AccessTokenDetails;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class TokenDecoder {
    TokenProperties tokenProperties;

    AccessTokenDetails decodeToken(final String token) {
        final AccessTokenDetails.AccessTokenDetailsBuilder tokenBuilder = AccessTokenDetails.builder();

        DecodedJWT decode = null;
        try {
            decode = JWT.decode(token);
            JWT
                    .require(Algorithm.HMAC256(tokenProperties.getSecretKey()))
                    .build()
                    .verify(decode);

            setClaims(tokenBuilder, decode);
        } catch (final TokenExpiredException exception) {
            setClaims(tokenBuilder, decode);
        } catch (final JWTVerificationException exception) {
            tokenBuilder.verified(false);
        }

        return tokenBuilder.build();
    }

    private void setClaims(final AccessTokenDetails.AccessTokenDetailsBuilder tokenBuilder, final DecodedJWT decodedJWT) {
        tokenBuilder
                .refreshTokenId(decodedJWT.getClaim("refreshTokenId").asLong())
                .userId(decodedJWT.getClaim("userId").asLong())
                .expiresAt(decodedJWT.getExpiresAtAsInstant());
    }
}
