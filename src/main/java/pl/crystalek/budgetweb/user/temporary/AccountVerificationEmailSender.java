package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AccountVerificationEmailSender {
    AccountConfirmationProperties accountConfirmationProperties;
    TemporaryUserRepository temporaryUserRepository;
    EmailSender emailSender;

    ResponseAPI<AccountConfirmationResendEmailResponseMessage> resendEmail(final String id) {
        final UUID uuid = UUID.fromString(id);
        final Optional<TemporaryUser> userOptional = temporaryUserRepository.findById(uuid);
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED);
        }

        final TemporaryUser user = userOptional.get();
        if (Instant.now().isAfter(user.getExpireAt())) {
            return new ResponseAPI<>(false, AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED);
        }

        sendVerificationEmail(user);
        return new ResponseAPI<>(true, AccountConfirmationResendEmailResponseMessage.SUCCESS);
    }

    void sendVerificationEmail(final TemporaryUser user) {
        final EmailContent emailContent = EmailContent.ofBasicEmail(accountConfirmationProperties, user.getEmail(), user.getId().toString());
        emailSender.send(emailContent);
    }
}
