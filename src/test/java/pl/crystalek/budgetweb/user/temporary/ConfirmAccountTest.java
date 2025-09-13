package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.crystalek.budgetweb.auth.response.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ConfirmAccountTest {
    @Mock TemporaryUserRepository temporaryUserRepository;
    @Mock UserService userService;
    @InjectMocks ConfirmAccount confirmAccount;

    @Test
    void shouldReturnSuccess() {
        // given
        final UUID id = UUID.randomUUID();
        final TemporaryUser user = mock(TemporaryUser.class);
        when(temporaryUserRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        final ResponseAPI<AccountConfirmationResponseMessage> response = confirmAccount.confirmAccount(id.toString());

        // then
        assertTrue(response.isSuccess());
        assertEquals(AccountConfirmationResponseMessage.SUCCESS, response.getMessage());
        verify(userService, times(1)).createUser(user);
        verify(temporaryUserRepository, times(1)).delete(user);
    }

    @Test
    void shouldReturnTokenExpiredWhenUserNotFound() {
        // given
        final UUID id = UUID.randomUUID();
        when(temporaryUserRepository.findById(id)).thenReturn(Optional.empty());

        // when
        final ResponseAPI<AccountConfirmationResponseMessage> response = confirmAccount.confirmAccount(id.toString());

        // then
        assertFalse(response.isSuccess());
        assertEquals(AccountConfirmationResponseMessage.TOKEN_EXPIRED, response.getMessage());
        verifyNoInteractions(userService);
        verify(temporaryUserRepository, never()).delete(any());
    }
}