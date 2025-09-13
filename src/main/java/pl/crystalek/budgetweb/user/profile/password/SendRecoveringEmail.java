package pl.crystalek.budgetweb.user.profile.password;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserDTO;
import pl.crystalek.budgetweb.user.profile.password.response.PasswordRecoveryResponseMessage;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SendRecoveringEmail {
    UserService userService;
    EmailSender emailSender;
    EntityManager entityManager;
    PasswordRecoveryProperties passwordRecoveryProperties;
    ConfirmationTokenService confirmationTokenService;

    ResponseAPI<PasswordRecoveryResponseMessage> sendRecoveringEmail(final String email) {
        final Optional<UserDTO> userOptional = userService.getUserDTO(email);
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, PasswordRecoveryResponseMessage.USER_NOT_FOUND);
        }

        final UUID token = getRecoveringToken(email, userOptional.get());
        final EmailContent emailContent = EmailContent.ofBasicEmail(passwordRecoveryProperties, email, token.toString());
        emailSender.send(emailContent);
        return new ResponseAPI<>(true, PasswordRecoveryResponseMessage.SUCCESS);
    }

    private UUID getRecoveringToken(final String email, final UserDTO userDTO) {
        final Instant emailExpireTime = Instant.now().plus(passwordRecoveryProperties.getEmailExpireTime());
        final User reference = entityManager.getReference(User.class, userDTO.id());
        final Optional<ConfirmationToken> confirmationToken = confirmationTokenService.getConfirmationToken(email, ConfirmationTokenType.PASSWORD_RECOVERY);
        if (confirmationToken.isPresent()) {
            return confirmationToken.get().getId();
        }

        return confirmationTokenService.createToken(reference, emailExpireTime, ConfirmationTokenType.PASSWORD_RECOVERY).getId();
    }
}
