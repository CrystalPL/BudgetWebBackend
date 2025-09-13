package pl.crystalek.budgetweb.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.response.LoginResponse;
import pl.crystalek.budgetweb.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.token.TokenFacade;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;
import pl.crystalek.budgetweb.user.temporary.TemporaryUserFacade;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthenticationServiceTest {
    @Mock AuthenticationProvider authenticateProvider;
    @Mock TemporaryUserFacade temporaryUserFacade;
    @Mock TokenFacade tokenFacade;
    @Mock CookieService cookieService;
    @Mock BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock HttpServletResponse response;
    @InjectMocks AuthenticationService authenticationService;

    LoginRequest loginRequest;
    DeviceInfo deviceInfo;
    TemporaryUser temporaryUser;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password", true);
        deviceInfo = new DeviceInfo("Windows", "Chrome");
        temporaryUser = new TemporaryUser("test@example.com", "hashedPassword", "testUser", false, Instant.now().plusSeconds(3600));
        temporaryUser.setId(UUID.randomUUID());
    }

    @Test
    void shouldReturnSuccessWhenCredentialsAreValid() {
        when(temporaryUserFacade.findByEmail(loginRequest.email())).thenReturn(Optional.empty());
        when(tokenFacade.createRefreshAndAccessToken(loginRequest, deviceInfo)).thenReturn("accessToken");

        LoginResponse result = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);

        final ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticateProvider).authenticate(captor.capture());

        verify(tokenFacade).createRefreshAndAccessToken(loginRequest, deviceInfo);
        verify(cookieService).createCookieAndAddToResponse("accessToken", loginRequest.rememberMe(), response);

        assertTrue(result.isSuccess());
        assertEquals(LoginResponseMessage.SUCCESS, result.getMessage());
        assertEquals(loginRequest.email(), captor.getValue().getPrincipal());
        assertEquals(loginRequest.password(), captor.getValue().getCredentials());
        assertEquals(LoginResponseMessage.SUCCESS, result.getMessage());
    }

    @Test
    void shouldReturnBadCredentialsWhenPasswordIsIncorrect() {
        when(temporaryUserFacade.findByEmail(loginRequest.email())).thenReturn(Optional.empty());
        doThrow(new BadCredentialsException("")).when(authenticateProvider).authenticate(any());

        LoginResponse result = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);

        assertFalse(result.isSuccess());
        assertEquals(LoginResponseMessage.BAD_CREDENTIALS, result.getMessage());

        verifyNoInteractions(tokenFacade, cookieService);
    }

    @Test
    void shouldReturnUserNotExistWhenUserNotFound() {
        when(temporaryUserFacade.findByEmail(loginRequest.email())).thenReturn(Optional.empty());
        doThrow(new UsernameNotFoundException("")).when(authenticateProvider).authenticate(any());

        LoginResponse result = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);

        assertFalse(result.isSuccess());
        assertEquals(LoginResponseMessage.USER_NOT_EXIST, result.getMessage());

        verifyNoInteractions(tokenFacade, cookieService);
    }

    @Test
    void shouldReturnAccountNotConfirmedWhenUserIsTemporary() {
        when(temporaryUserFacade.findByEmail(loginRequest.email())).thenReturn(Optional.of(temporaryUser));
        when(bCryptPasswordEncoder.matches(loginRequest.password(), temporaryUser.getPassword())).thenReturn(true);

        LoginResponse result = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);

        assertFalse(result.isSuccess());
        assertEquals(LoginResponseMessage.ACCOUNT_NOT_CONFIRMED, result.getMessage());
        assertEquals(temporaryUser.getId(), result.getRegistrationToken());

        verifyNoInteractions(authenticateProvider, tokenFacade, cookieService);
    }

    @Test
    void shouldReturnBadCredentialsWhenTemporaryUserPasswordDoesNotMatch() {
        when(temporaryUserFacade.findByEmail(loginRequest.email())).thenReturn(Optional.of(temporaryUser));
        when(bCryptPasswordEncoder.matches(loginRequest.password(), temporaryUser.getPassword())).thenReturn(false);

        final LoginResponse result = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);
        assertFalse(result.isSuccess());
        assertEquals(LoginResponseMessage.BAD_CREDENTIALS, result.getMessage());
        verifyNoInteractions(authenticateProvider, tokenFacade, cookieService);
    }

    @Test
    void shouldCallTokenFacadeToLogout() {
        long tokenId = 123L;

        authenticationService.logout(tokenId);

        verify(tokenFacade).logoutByRefreshTokenId(tokenId);
    }
}