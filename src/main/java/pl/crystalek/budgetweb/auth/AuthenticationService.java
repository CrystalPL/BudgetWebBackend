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
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.LoginResponse;
import pl.crystalek.budgetweb.auth.controller.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.auth.cookie.CookieService;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.token.TokenService;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;
import pl.crystalek.budgetweb.user.temporary.TemporaryUserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationService {
    AuthenticationProvider authenticateProvider;
    TemporaryUserService temporaryUserService;
    TokenService tokenService;
    CookieService cookieService;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginResponse authenticateAndAddCookie(final LoginRequest loginRequest, final HttpServletResponse response, final DeviceInfo deviceInfo) {
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        try {
            final Optional<TemporaryUser> userOptional = temporaryUserService.findByEmail(loginRequest.email());
            if (userOptional.isPresent()) {
                final TemporaryUser temporaryUser = userOptional.get();
                if (!bCryptPasswordEncoder.matches(loginRequest.password(), temporaryUser.getPassword())) {
                    throw new BadCredentialsException("");
                }

                return new LoginResponse(false, LoginResponseMessage.ACCOUNT_NOT_CONFIRMED, temporaryUser.getId());
            }

            authenticateProvider.authenticate(authenticationToken);
            final String accessToken = tokenService.createRefreshAndAccessToken(loginRequest, deviceInfo);
            cookieService.createCookieAndAddToResponse(accessToken, loginRequest.rememberMe(), response);

            return new LoginResponse(true, LoginResponseMessage.SUCCESS);
        } catch (final BadCredentialsException exception) {
            return new LoginResponse(false, LoginResponseMessage.BAD_CREDENTIALS);
        } catch (final UsernameNotFoundException exception) {
            return new LoginResponse(false, LoginResponseMessage.USER_NOT_EXIST);
        }
    }

    public void logout(final long tokenId) {
        tokenService.logoutByRefreshTokenId(tokenId);
    }
}
