package pl.crystalek.budgetweb.auth.token;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.token.model.AccessTokenDetails;
import pl.crystalek.budgetweb.auth.token.model.RefreshToken;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserDTO;

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
    EntityManager entityManager;

    //tworzy nowy refresh i access token
    //zwraca access token
    @Transactional
    public String createRefreshAndAccessToken(final LoginRequest loginRequest, final DeviceInfo deviceInfo) {
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

        return Optional.of(tokenCreator.create(userId, refreshTokenId));
    }

    public void logoutByRefreshTokenId(final long tokenId) {
        tokenRepository.deleteById(tokenId);
    }

    public void logoutUserFromDevices(final User user) {
        tokenRepository.deleteByUser(user);
    }
}
