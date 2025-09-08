package pl.crystalek.budgetweb.user.email;

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
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.response.ChangeEmailResponseMessage;

import java.time.Instant;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SendChangeEmail {
    UserService userService;
    ChangeEmailRepository repository;
    ConfirmationTokenService confirmationTokenService;
    EntityManager entityManager;
    ChangeEmailProperties changeEmailProperties;
    EmailSender emailSender;

    ResponseAPI<ChangeEmailResponseMessage> changeEmail(final long userId, final ChangeEmailRequest changeEmailRequest) {
        final ResponseAPI<ChangeEmailResponseMessage> validationResult = validateRequest(userId, changeEmailRequest);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        deleteExistsToken(userId);
        entityManager.flush();
        final ConfirmationToken token = getConfirmationToken(userId);
        repository.save(new ChangeEmail(token, changeEmailRequest.email()));
        sendEmail(token, changeEmailRequest);

        return new ResponseAPI<>(true, ChangeEmailResponseMessage.SUCCESS);
    }

    private ResponseAPI<ChangeEmailResponseMessage> validateRequest(final long userId, final ChangeEmailRequest changeEmailRequest) {
        if (!userService.isCorrectPassword(userId, changeEmailRequest.password())) {
            return new ResponseAPI<>(false, ChangeEmailResponseMessage.BAD_CREDENTIALS);
        }

        final String newEmail = changeEmailRequest.email();
        if (userService.isUserExists(newEmail)) {
            return new ResponseAPI<>(false, ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS);
        }

        if (repository.existsByNewEmailAndConfirmationToken_User_IdNot(newEmail, userId)) {
            return new ResponseAPI<>(false, ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS);
        }

        return new ResponseAPI<>(true, ChangeEmailResponseMessage.SUCCESS);
    }

    private void deleteExistsToken(final long userId) {
        repository.findByConfirmationToken_User_Id(userId).ifPresent(repository::delete);
    }

    private ConfirmationToken getConfirmationToken(final long userId) {
        final User userReference = entityManager.getReference(User.class, userId);
        final Instant expireAt = Instant.now().plus(changeEmailProperties.getEmailExpireTime());
        return confirmationTokenService.createToken(userReference, expireAt, ConfirmationTokenType.CHANGE_EMAIL);
    }

    private void sendEmail(final ConfirmationToken token, final ChangeEmailRequest changeEmailRequest) {
        final EmailContent emailContent = EmailContent.ofBasicEmail(changeEmailProperties, changeEmailRequest.confirmEmail(), token.getId().toString());
        emailSender.send(emailContent);
    }
}
