package pl.crystalek.budgetweb.user.email;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.response.ConfirmEmailChangingResponseMessage;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ConfirmEmailChangingTest {
    @Mock ConfirmationTokenService confirmationTokenService;
    @Mock ChangeEmailRepository repository;
    @InjectMocks ConfirmEmailChanging confirmEmailChanging;

    @Test
    void shouldSuccessfullyChangeEmailWhenTokenIsValid() {
        final User user = new User();
        final ConfirmationToken confirmationToken = new ConfirmationToken(user, null, null);
        when(confirmationTokenService.getConfirmationToken(any(UUID.class), any())).thenReturn(Optional.of(confirmationToken));

        final ChangeEmail changeEmail = new ChangeEmail(null, "nowyEmail");
        when(repository.findById(any())).thenReturn(Optional.of(changeEmail));

        final ResponseAPI<ConfirmEmailChangingResponseMessage> response = confirmEmailChanging.confirmEmailChanging(UUID.randomUUID().toString());

        assertEquals(ConfirmEmailChangingResponseMessage.SUCCESS, response.getMessage());
        assertTrue(response.isSuccess());
        assertEquals("nowyEmail", user.getEmail());
    }

    @Test
    void shouldReturnTokenExpiredWhenTokenNotFound() {
        when(confirmationTokenService.getConfirmationToken(any(UUID.class), any())).thenReturn(Optional.empty());

        final ResponseAPI<ConfirmEmailChangingResponseMessage> response = confirmEmailChanging.confirmEmailChanging(UUID.randomUUID().toString());

        assertEquals(ConfirmEmailChangingResponseMessage.TOKEN_EXPIRED, response.getMessage());
        assertFalse(response.isSuccess());
    }

    @Test
    void shouldReturnTokenNotFoundWhenChangeEmailNotFound() {
        when(confirmationTokenService.getConfirmationToken(any(UUID.class), any())).thenReturn(Optional.of(new ConfirmationToken()));
        when(repository.findById(any())).thenReturn(Optional.empty());

        final ResponseAPI<ConfirmEmailChangingResponseMessage> response = confirmEmailChanging.confirmEmailChanging(UUID.randomUUID().toString());

        assertEquals(ConfirmEmailChangingResponseMessage.TOKEN_NOT_FOUND, response.getMessage());
        assertFalse(response.isSuccess());
    }
}