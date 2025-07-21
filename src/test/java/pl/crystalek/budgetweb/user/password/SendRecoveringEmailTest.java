package pl.crystalek.budgetweb.user.password;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserDTO;
import pl.crystalek.budgetweb.user.password.response.PasswordRecoveryResponseMessage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
class SendRecoveringEmailTest {
    final static String EMAIL = "testEmail@s.pl";
    final static UUID TOKEN_UUID = UUID.randomUUID();
    final static User USER = new User();
    static final long USER_ID = 1L;

    @Mock
    UserService userService;
    @Mock
    EmailSender emailSender;
    @Mock
    EntityManager entityManager;
    @Mock
    PasswordRecoveryProperties passwordRecoveryProperties;
    @Mock
    ConfirmationTokenService confirmationTokenService;
    @Mock
    ConfirmationToken confirmationToken;
    MockedStatic<EmailContent> emailContentMockedStatic;

    SendRecoveringEmail sendRecoveringEmail;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(passwordRecoveryProperties.getEmailExpireTime()).thenReturn(Duration.of(30, ChronoUnit.MINUTES));
        when(entityManager.getReference(any(), any())).thenReturn(USER);
        when(confirmationToken.getId()).thenReturn(TOKEN_UUID);

        emailContentMockedStatic = mockStatic(EmailContent.class);
        emailContentMockedStatic.when(() -> EmailContent.ofBasicEmail(any(), any(), any())).thenReturn(null);

        doNothing().when(emailSender).send(any(EmailContent.class));

        sendRecoveringEmail = new SendRecoveringEmail(userService, emailSender, entityManager,
                passwordRecoveryProperties, confirmationTokenService);
    }

    @Test
    void shouldSendRecoveringEmailWhenTokenExists() {
        when(userService.getUserDTO(any())).thenReturn(Optional.of(new UserDTO(USER_ID, EMAIL)));
        when(confirmationTokenService.getConfirmationToken((String) isNull(), any())).thenReturn(Optional.of(confirmationToken));

        final ResponseAPI<PasswordRecoveryResponseMessage> response = sendRecoveringEmail.sendRecoveringEmail(null);
        assertTrue(response.isSuccess());
        assertEquals(PasswordRecoveryResponseMessage.SUCCESS, response.getMessage());
    }

    @Test
    void shouldSendRecoveringEmailAndCreateToken() {
        when(userService.getUserDTO(any())).thenReturn(Optional.of(new UserDTO(USER_ID, EMAIL)));
        when(confirmationTokenService.getConfirmationToken((String) isNull(), any())).thenReturn(Optional.empty());
        when(confirmationTokenService.createToken(any(), any(), any())).thenReturn(confirmationToken);

        final ResponseAPI<PasswordRecoveryResponseMessage> response = sendRecoveringEmail.sendRecoveringEmail(null);
        assertTrue(response.isSuccess());
        assertEquals(PasswordRecoveryResponseMessage.SUCCESS, response.getMessage());
    }

    @Test
    void shouldUserNotFoundWhenUserNotExists() {
        when(userService.getUserDTO(any())).thenReturn(Optional.empty());
        final ResponseAPI<PasswordRecoveryResponseMessage> response = sendRecoveringEmail.sendRecoveringEmail(null);
        assertFalse(response.isSuccess());
        assertEquals(PasswordRecoveryResponseMessage.USER_NOT_FOUND, response.getMessage());
    }

    @AfterEach
    void tearDown() {
        if (emailContentMockedStatic != null) {
            emailContentMockedStatic.close();
        }
    }
}