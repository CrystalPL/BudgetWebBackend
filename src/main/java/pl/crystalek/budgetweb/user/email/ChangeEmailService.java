package pl.crystalek.budgetweb.user.email;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.response.ChangeEmailResponseMessage;
import pl.crystalek.budgetweb.user.response.ConfirmEmailChangingResponseMessage;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChangeEmailService {
    ChangeEmailRepository repository;
    ConfirmationTokenService confirmationTokenService;
    ChangeEmailProperties changeEmailProperties;
    EntityManager entityManager;
    EmailSender emailSender;
    UserService userService;

    public boolean isEmailExists(final String email) {
        return repository.existsByNewEmail(email);
    }

    public boolean isEmailChangingWaitingToConfirm(final long userId) {
        return repository.existsByConfirmationToken_User_Id(userId);
    }

    public ResponseAPI<ChangeEmailResponseMessage> changeEmail(final long userId, final String newEmail, final String password) {
        if (!userService.isCorrectPassword(userId, password)) {
            return new ResponseAPI<>(false, ChangeEmailResponseMessage.BAD_CREDENTIALS);
        }

        if (userService.isUserExists(newEmail)) {
            return new ResponseAPI<>(false, ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS);
        }

        if (repository.existsByNewEmailAndConfirmationToken_User_IdNot(newEmail, userId)) {
            return new ResponseAPI<>(false, ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS);
        }

        repository.findByConfirmationToken_User_Id(userId).ifPresent(changeEmail -> {
            repository.delete(changeEmail);
            confirmationTokenService.delete(changeEmail.getConfirmationToken());
        });

        final User userReference = entityManager.getReference(User.class, userId);
        final Instant expireAt = Instant.now().plus(changeEmailProperties.getEmailExpireTime());
        final ConfirmationToken token = confirmationTokenService.createToken(userReference, expireAt, ConfirmationTokenType.CHANGE_EMAIL);

        repository.save(new ChangeEmail(token, newEmail));

        final EmailContent emailContent = EmailContent.ofBasicEmail(changeEmailProperties, newEmail, token.getId().toString());
        emailSender.send(emailContent);

        return new ResponseAPI<>(true, ChangeEmailResponseMessage.SUCCESS);
    }

    public ResponseAPI<ConfirmEmailChangingResponseMessage> confirmEmailChanging(final String changingToken) {
        final UUID token = UUID.fromString(changingToken);
        final Optional<ConfirmationToken> tokenOptional = confirmationTokenService.getConfirmationToken(token, ConfirmationTokenType.CHANGE_EMAIL);
        if (tokenOptional.isEmpty()) {
            return new ResponseAPI<>(false, ConfirmEmailChangingResponseMessage.TOKEN_EXPIRED);
        }

        final ConfirmationToken confirmationToken = tokenOptional.get();
        final Optional<ChangeEmail> changeEmailOptional = repository.findById(confirmationToken.getId());
        if (changeEmailOptional.isEmpty()) {
            return new ResponseAPI<>(false, ConfirmEmailChangingResponseMessage.TOKEN_NOT_FOUND);
        }

        final ChangeEmail changeEmail = changeEmailOptional.get();
        confirmationToken.getUser().setEmail(changeEmail.getNewEmail());
        repository.delete(changeEmail);
        confirmationTokenService.delete(confirmationToken);

        return new ResponseAPI<>(true, ConfirmEmailChangingResponseMessage.SUCCESS);
    }

}
