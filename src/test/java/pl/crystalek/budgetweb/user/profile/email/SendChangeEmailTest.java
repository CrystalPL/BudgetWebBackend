package pl.crystalek.budgetweb.user.profile.email;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.response.ChangeEmailResponseMessage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class SendChangeEmailTest {
    @Mock UserService userService;
    @Mock ChangeEmailRepository repository;
    @Mock ConfirmationTokenService confirmationTokenService;
    @Mock EntityManager entityManager;
    @Mock ChangeEmailProperties changeEmailProperties;
    @Mock EmailSender emailSender;
    @Mock ConfirmationToken confirmationToken;
    @InjectMocks SendChangeEmail sendChangeEmail;
    MockedStatic<EmailContent> emailContentMockedStatic;

    @BeforeEach
    void setUp() {
        emailContentMockedStatic = mockStatic(EmailContent.class);
    }

    @Test
    void shouldSuccessfullySendChangeEmailWhenRequestIsValid() {
        when(userService.isCorrectPassword(anyLong(), anyString())).thenReturn(true);
        when(userService.isUserExists(anyString())).thenReturn(false);
        when(repository.existsByNewEmailAndConfirmationToken_User_IdNot(anyString(), anyLong())).thenReturn(false);
        when(confirmationToken.getId()).thenReturn(UUID.randomUUID());
        when(confirmationTokenService.createToken(any(), any(), any())).thenReturn(confirmationToken);
        emailContentMockedStatic.when(() -> EmailContent.ofBasicEmail(any(), anyString(), anyString())).thenReturn(null);

        final ResponseAPI<ChangeEmailResponseMessage> response = sendChangeEmail.changeEmail(1, new ChangeEmailRequest("a", "a", "c"));

        assertTrue(response.isSuccess());
        assertEquals(ChangeEmailResponseMessage.SUCCESS, response.getMessage());
    }

    @Test
    void shouldReturnBadCredentialsWhenPasswordIsIncorrect() {
        when(userService.isCorrectPassword(anyLong(), anyString())).thenReturn(false);

        final ResponseAPI<ChangeEmailResponseMessage> response = sendChangeEmail.changeEmail(1, new ChangeEmailRequest("a", "a", "c"));

        assertFalse(response.isSuccess());
        assertEquals(ChangeEmailResponseMessage.BAD_CREDENTIALS, response.getMessage());
    }

    @Test
    void shouldReturnEmailAlreadyExistsWhenEmailIsUsedByAnotherUser() {
        when(userService.isCorrectPassword(anyLong(), anyString())).thenReturn(true);
        when(userService.isUserExists(anyString())).thenReturn(true);

        final ResponseAPI<ChangeEmailResponseMessage> response = sendChangeEmail.changeEmail(1, new ChangeEmailRequest("a", "a", "c"));

        assertFalse(response.isSuccess());
        assertEquals(ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS, response.getMessage());
    }

    @Test
    void shouldReturnEmailAlreadyExistsWhenEmailIsAwaitingConfirmation() {
        when(userService.isCorrectPassword(anyLong(), anyString())).thenReturn(true);
        when(userService.isUserExists(anyString())).thenReturn(false);
        when(repository.existsByNewEmailAndConfirmationToken_User_IdNot(anyString(), anyLong())).thenReturn(true);

        final ResponseAPI<ChangeEmailResponseMessage> response = sendChangeEmail.changeEmail(1, new ChangeEmailRequest("a", "a", "c"));

        assertFalse(response.isSuccess());
        assertEquals(ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS, response.getMessage());
    }

    @AfterEach
    void tearDown() {
        if (emailContentMockedStatic != null) {
            emailContentMockedStatic.close();
        }
    }
}