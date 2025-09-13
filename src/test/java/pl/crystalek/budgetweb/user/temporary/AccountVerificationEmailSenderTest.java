package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.crystalek.budgetweb.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AccountVerificationEmailSenderTest {
    @Mock TemporaryUserRepository temporaryUserRepository;
    @Mock EmailSender emailSender;
    @InjectMocks AccountVerificationEmailSender sender;

    @Test
    void shouldReturnAccountConfirmedWhenUserNotFound() {
        // given
        final UUID id = UUID.randomUUID();
        when(temporaryUserRepository.findById(id)).thenReturn(Optional.empty());

        // when
        final ResponseAPI<AccountConfirmationResendEmailResponseMessage> response = sender.resendEmail(id.toString());

        // then
        assertFalse(response.isSuccess());
        assertEquals(AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED, response.getMessage());
        verifyNoInteractions(emailSender);
    }

    @Test
    void shouldReturnTokenExpiredWhenTokenIsExpired() {
        // given
        final UUID id = UUID.randomUUID();
        final TemporaryUser user = mock(TemporaryUser.class);
        when(user.getExpireAt()).thenReturn(Instant.now().minus(Duration.ofDays(1)));
        when(temporaryUserRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        final ResponseAPI<AccountConfirmationResendEmailResponseMessage> response = sender.resendEmail(id.toString());

        // then
        assertFalse(response.isSuccess());
        assertEquals(AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED, response.getMessage());
        verifyNoInteractions(emailSender);
    }

    @Test
    void shouldSendEmailAndReturnSuccessWhenTokenIsValid() {
        // given
        final UUID id = UUID.randomUUID();

        final TemporaryUser user = mock(TemporaryUser.class);
        when(user.getExpireAt()).thenReturn(Instant.now().plus(Duration.ofDays(1)));
        when(user.getId()).thenReturn(id);
        when(temporaryUserRepository.findById(id)).thenReturn(Optional.of(user));

        final EmailContent emailContent = mock(EmailContent.class);

        @Cleanup MockedStatic<EmailContent> mocked = mockStatic(EmailContent.class);
        mocked.when(() -> EmailContent.ofBasicEmail(any(), any(), any())).thenReturn(emailContent);

        // when
        final ResponseAPI<AccountConfirmationResendEmailResponseMessage> response = sender.resendEmail(id.toString());

        // then
        assertTrue(response.isSuccess());
        assertEquals(AccountConfirmationResendEmailResponseMessage.SUCCESS, response.getMessage());
        verify(emailSender, times(1)).send(emailContent);
    }
}