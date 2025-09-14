package pl.crystalek.budgetweb.token;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.request.LoginRequest;
import pl.crystalek.budgetweb.token.model.AccessTokenDetails;
import pl.crystalek.budgetweb.user.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TokenFacade {
    TokenRepository tokenRepository;
    CreateAccessToken createAccessToken;
    CreateRefreshAndAccessToken createRefreshAndAccessToken;
    TokenDecoder tokenDecoder;

    @Transactional
    public String createRefreshAndAccessToken(final LoginRequest loginRequest, final DeviceInfo deviceInfo) {
        return createRefreshAndAccessToken.createRefreshAndAccessToken(loginRequest, deviceInfo);
    }

    public Optional<String> createAccessToken(final AccessTokenDetails tokenDetails) {
        return createAccessToken.createAndGetAccessToken(tokenDetails);
    }

    public void logoutByRefreshTokenId(final long tokenId) {
        tokenRepository.deleteById(tokenId);
    }

    public void logoutUserFromDevices(final User user) {
        tokenRepository.deleteByUser(user);
    }

    public AccessTokenDetails decodeToken(final String token) {
        return tokenDecoder.decodeToken(token);
    }
}
