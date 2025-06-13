package pl.crystalek.budgetweb.auth.passwordrecovery;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.password.response.PasswordRecoveryResponseMessage;
import pl.crystalek.budgetweb.auth.controller.password.response.PasswordResetResponseMessage;
import pl.crystalek.budgetweb.auth.token.TokenService;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserDTO;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PasswordRecoveryService {
    PasswordRecoveryProperties passwordRecoveryProperties;
    UserService userService;
    EmailSender emailSender;
    PasswordEncoder passwordEncoder;
    TokenService tokenService;
    ConfirmationTokenService confirmationTokenService;
    EntityManager entityManager;

    public ResponseAPI<PasswordRecoveryResponseMessage> sendRecoveringEmail(final String email) {
        final Optional<UserDTO> userOptional = userService.getUserDTO(email);
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, PasswordRecoveryResponseMessage.USER_NOT_FOUND);
        }

        final Instant emailExpireTime = Instant.now().plus(passwordRecoveryProperties.getEmailExpireTime());
        final User reference = entityManager.getReference(User.class, userOptional.get().id());
        final UUID token = confirmationTokenService.getConfirmationToken(email, ConfirmationTokenType.PASSWORD_RECOVERY)
                .orElseGet(() -> confirmationTokenService.getToken(reference, emailExpireTime, ConfirmationTokenType.PASSWORD_RECOVERY)).getId();

        final EmailContent emailContent = EmailContent.ofBasicEmail(passwordRecoveryProperties, email, token.toString());
        emailSender.send(emailContent);
        return new ResponseAPI<>(true, PasswordRecoveryResponseMessage.SUCCESS);
    }

    public ResponseAPI<PasswordResetResponseMessage> resetPassword(final String stringToken, final String password, final String confirmPassword) {
        final UUID token = UUID.fromString(stringToken);

        final Optional<ConfirmationToken> tokenOptional = confirmationTokenService.getConfirmationToken(token, ConfirmationTokenType.PASSWORD_RECOVERY);
        if (tokenOptional.isEmpty()) {
            return new ResponseAPI<>(false, PasswordResetResponseMessage.TOKEN_EXPIRED);
        }

        if (!password.equals(confirmPassword)) {
            return new ResponseAPI<>(false, PasswordResetResponseMessage.PASSWORD_MISMATCH);
        }

        final ConfirmationToken passwordRecovery = tokenOptional.get();
        final User user = passwordRecovery.getUser();
        user.setPassword(passwordEncoder.encode(password));
        confirmationTokenService.delete(passwordRecovery);
        tokenService.logoutUserFromDevices(user);

        return new ResponseAPI<>(true, PasswordResetResponseMessage.SUCCESS);
    }

    //TODO
//    @Scheduled(fixedRateString = "#{T(java.time.Duration).parse('${password-recovery.email.config.cleanUpExpiredEmails}').toMillis()}")
//    private void removeExpiredEmails() {
//        confirmationTokenService.clearByExpireTime(ConfirmationTokenType.PASSWORD_RECOVERY);
//    }
}
