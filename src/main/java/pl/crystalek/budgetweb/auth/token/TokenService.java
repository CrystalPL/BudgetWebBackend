package pl.crystalek.budgetweb.auth.token;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.model.LoginRequest;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.token.model.AccessTokenDetails;
import pl.crystalek.budgetweb.auth.token.model.RefreshToken;
import pl.crystalek.budgetweb.user.User;
import pl.crystalek.budgetweb.user.UserService;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TokenService {
    TokenRepository tokenRepository;
    TokenCreator tokenCreator;
    UserService userService;
    TokenProperties tokenProperties;

    //tworzy nowy refresh i access token
    //zwraca access token
    @Transactional
    public String createRefreshAndAccessToken(final LoginRequest loginRequest, final DeviceInfo deviceInfo) {
        final User user = userService.getUserByEmail(loginRequest.email()).get(); //zalogował się, dlatego ignoruje optionala
        final Instant expireAt = Instant.now().plus(tokenProperties.getRefreshTokenExpireTime());
        if (loginRequest.rememberMe()) {
            //użytkownik zalogowany z zapamiętaj mnie, może byc jednocześnie zalogowany tylko na jednym urządzeniu, pozostałe będą wylogowywane
            tokenRepository.deleteByUserAndRememberMeIsTrue(user);
        }

        final RefreshToken refreshToken = tokenRepository.save(new RefreshToken(deviceInfo, expireAt, loginRequest.rememberMe(), user));

        return tokenCreator.create(user.getId(), refreshToken.getId(), user.getUserRole());
    }

    public Optional<String> createAccessToken(final AccessTokenDetails tokenDetails) {
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

        return Optional.of(tokenCreator.create(userId, refreshTokenId, tokenDetails.getRole()));
    }

    public void logoutUserFromDevices(final User user) {
        tokenRepository.deleteByUser(user);
    }
}
