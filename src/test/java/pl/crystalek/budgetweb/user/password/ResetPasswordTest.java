package pl.crystalek.budgetweb.user.password;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.token.TokenFacade;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.password.response.PasswordResetResponseMessage;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ResetPasswordTest {
    static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    @Mock ConfirmationTokenService confirmationTokenService;
    @Mock TokenFacade tokenFacade;
    @Mock ConfirmationToken confirmationToken;

    ResetPassword resetPassword;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(confirmationTokenService.getConfirmationToken(any(UUID.class), any())).thenReturn(Optional.of(confirmationToken));
        resetPassword = new ResetPassword(confirmationTokenService, tokenFacade, PASSWORD_ENCODER);
    }

    @Test
    void shouldReturnTokenExpiredWhenTokenIsEmpty() {
        when(confirmationTokenService.getConfirmationToken(any(UUID.class), any())).thenReturn(Optional.empty());

        final ResponseAPI<PasswordResetResponseMessage> response = resetPassword.resetPassword(UUID.randomUUID().toString(), null, null);
        assertFalse(response.isSuccess());
        assertEquals(PasswordResetResponseMessage.TOKEN_EXPIRED, response.getMessage());
    }

    @Test
    void shouldReturnSuccess() {
        final User user = new User();
        when(confirmationToken.getUser()).thenReturn(user);
        doNothing().when(confirmationTokenService).delete(any());
        doNothing().when(tokenFacade).logoutUserFromDevices(any());

        final ResponseAPI<PasswordResetResponseMessage> response = resetPassword.resetPassword(UUID.randomUUID().toString(), "123", "123");
        assertTrue(response.isSuccess());
        assertEquals(PasswordResetResponseMessage.SUCCESS, response.getMessage());
        assertTrue(PASSWORD_ENCODER.matches("123", user.getPassword()));
    }
}