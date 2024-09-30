package pl.crystalek.budgetweb.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.model.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.LoginResponseMessage;
import pl.crystalek.budgetweb.auth.cookie.CookieService;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.token.TokenService;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserRole;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationService {
    AuthenticationProvider authenticateProvider;
    TokenService tokenService;
    CookieService cookieService;

    public ResponseAPI<LoginResponseMessage> authenticateAndAddCookie(final LoginRequest loginRequest, final HttpServletResponse response, final DeviceInfo deviceInfo) {
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        try {
            final Authentication authenticate = authenticateProvider.authenticate(authenticationToken);
            final boolean accountNotConfirmed = authenticate.getAuthorities().stream().anyMatch(UserRole.GUEST::equals);

            final String accessToken = tokenService.createRefreshAndAccessToken(loginRequest, deviceInfo);
            cookieService.createCookieAndAddToResponse(accessToken, loginRequest.rememberMe(), response);

            if (accountNotConfirmed) {
                return new ResponseAPI<>(false, LoginResponseMessage.ACCOUNT_NOT_CONFIRMED);
            }

            return new ResponseAPI<>(true, LoginResponseMessage.SUCCESS);
        } catch (final BadCredentialsException exception) {
            return new ResponseAPI<>(false, LoginResponseMessage.BAD_CREDENTIALS);
        } catch (final UsernameNotFoundException exception) {
            return new ResponseAPI<>(false, LoginResponseMessage.USER_NOT_EXIST);
        }
    }

    public void logout(final long tokenId) {
        tokenService.logoutByRefreshTokenId(tokenId);
    }
}
