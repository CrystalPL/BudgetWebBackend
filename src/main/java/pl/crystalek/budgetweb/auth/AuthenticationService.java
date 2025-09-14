package pl.crystalek.budgetweb.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.response.LoginResponse;
import pl.crystalek.budgetweb.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.token.TokenFacade;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;
import pl.crystalek.budgetweb.user.temporary.TemporaryUserFacade;

import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AuthenticationService {
    AuthenticationProvider authenticateProvider;
    TemporaryUserFacade temporaryUserFacade;
    TokenFacade tokenFacade;
    CookieService cookieService;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    LoginResponse authenticateAndAddCookie(final LoginRequest loginRequest, final HttpServletResponse response, final DeviceInfo deviceInfo) {
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        try {
            final Optional<TemporaryUser> userOptional = temporaryUserFacade.findByEmail(loginRequest.email());
            if (userOptional.isPresent()) {
                return getErrorWhenUserNotConfirmed(loginRequest, userOptional.get());
            }

            authenticateProvider.authenticate(authenticationToken);
            addCookieToResponse(loginRequest, response, deviceInfo);

            return new LoginResponse(true, LoginResponseMessage.SUCCESS);
        } catch (final BadCredentialsException exception) {
            return new LoginResponse(false, LoginResponseMessage.BAD_CREDENTIALS);
        } catch (final UsernameNotFoundException exception) {
            return new LoginResponse(false, LoginResponseMessage.USER_NOT_EXIST);
        }
    }

    private LoginResponse getErrorWhenUserNotConfirmed(final LoginRequest loginRequest, final TemporaryUser temporaryUser) {
        if (!bCryptPasswordEncoder.matches(loginRequest.password(), temporaryUser.getPassword())) {
            throw new BadCredentialsException("");
        }

        return new LoginResponse(false, LoginResponseMessage.ACCOUNT_NOT_CONFIRMED, temporaryUser.getId());
    }

    private void addCookieToResponse(final LoginRequest loginRequest, final HttpServletResponse response, final DeviceInfo deviceInfo) {
        final String accessToken = tokenFacade.createRefreshAndAccessToken(loginRequest, deviceInfo);
        cookieService.createCookieAndAddToResponse(accessToken, loginRequest.rememberMe(), response);
    }

    public void logout(final long tokenId) {
        tokenFacade.logoutByRefreshTokenId(tokenId);
    }
}
