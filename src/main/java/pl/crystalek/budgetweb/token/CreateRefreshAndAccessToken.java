package pl.crystalek.budgetweb.token;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.token.model.RefreshToken;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.user.auth.request.LoginRequest;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserDTO;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class CreateRefreshAndAccessToken {
    TokenRepository tokenRepository;
    UserService userService;
    TokenCreator tokenCreator;
    TokenProperties tokenProperties;
    EntityManager entityManager;

    //tworzy nowy refresh i access token
    //zwraca access token
    String createRefreshAndAccessToken(final LoginRequest loginRequest, final DeviceInfo deviceInfo) {
        final UserDTO userCredentialsDTO = userService.getUserDTO(loginRequest.email()).get();
        final Instant expireAt = Instant.now().plus(tokenProperties.getRefreshTokenExpireTime());
        if (loginRequest.rememberMe()) {
            //użytkownik zalogowany z zapamiętaj mnie, może byc jednocześnie zalogowany tylko na jednym urządzeniu, pozostałe będą wylogowywane
            tokenRepository.deleteByUser_IdAndRememberMeIsTrue(userCredentialsDTO.id());
        }

        final User reference = entityManager.getReference(User.class, userCredentialsDTO.id());
        final RefreshToken refreshToken = tokenRepository.save(new RefreshToken(deviceInfo, expireAt, loginRequest.rememberMe(), reference));

        return tokenCreator.create(userCredentialsDTO.id(), refreshToken.getId());
    }
}
