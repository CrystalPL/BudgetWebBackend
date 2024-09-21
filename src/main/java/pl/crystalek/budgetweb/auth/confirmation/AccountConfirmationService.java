package pl.crystalek.budgetweb.auth.confirmation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.User;
import pl.crystalek.budgetweb.user.UserRole;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountConfirmationService {
    AccountConfirmationProperties accountConfirmationProperties;
    ConfirmationTokenService confirmationTokenService;
    EmailSender emailSender;

    public ResponseAPI<AccountConfirmationResendEmailResponseMessage> resendEmail(final String emailAddress) {
        final Optional<ConfirmationToken> tokenOptional = confirmationTokenService.getConfirmationToken(emailAddress, ConfirmationTokenType.ACCOUNT_REGISTER);
        if (tokenOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED);
        }

        final ConfirmationToken accountConfirmation = tokenOptional.get();
        if (Instant.now().isAfter(accountConfirmation.getExpireAt())) {
            return new ResponseAPI<>(false,  AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED);
        }

        final EmailContent emailContent = EmailContent.ofBasicEmail(accountConfirmationProperties, emailAddress, accountConfirmation.getId().toString());
        emailSender.send(emailContent);
        return new ResponseAPI<>(true, AccountConfirmationResendEmailResponseMessage.SUCCESS);
    }

    public void sendVerificationEmail(final User user) {
        final Instant emailExpireTime = Instant.now().plus(accountConfirmationProperties.getEmailExpireTime());
        final UUID token = confirmationTokenService.getToken(user, emailExpireTime, ConfirmationTokenType.ACCOUNT_REGISTER).getId();

        final EmailContent emailContent = EmailContent.ofBasicEmail(accountConfirmationProperties, user.getEmail(), token.toString());
        emailSender.send(emailContent);
    }

    public ResponseAPI<AccountConfirmationResponseMessage> confirmAccount(final String confirmationToken) {
        final UUID token = UUID.fromString(confirmationToken);

        final Optional<ConfirmationToken> tokenOptional = confirmationTokenService.getConfirmationToken(token, ConfirmationTokenType.ACCOUNT_REGISTER);
        if (tokenOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResponseMessage.TOKEN_EXPIRED);
        }

        final ConfirmationToken accountConfirmation = tokenOptional.get();
        final User user = accountConfirmation.getUser();
        user.setUserRole(UserRole.USER);
        confirmationTokenService.delete(accountConfirmation);

        return new ResponseAPI<>(true, AccountConfirmationResponseMessage.SUCCESS);
    }

    @Scheduled(fixedRateString = "#{T(java.time.Duration).parse('${account.confirmation.email.config.cleanUpExpiredEmails}').toMillis()}")
    private void removeExpiredEmails() {
        confirmationTokenService.clearByExpireTime(ConfirmationTokenType.ACCOUNT_REGISTER);
    }
}
