package pl.crystalek.budgetweb.auth.confirmation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationResponseMessage;
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
    AccountConfirmationRepository accountConfirmationRepository;
    EmailSender emailSender;

    public ResponseAPI<AccountConfirmationResendEmailResponseMessage> resendEmail(final String emailAddress) {
        final Optional<AccountConfirmation> tokenOptional = accountConfirmationRepository.findByUser_Email(emailAddress);
        if (tokenOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED);
        }

        final AccountConfirmation accountConfirmation = tokenOptional.get();
        if (Instant.now().isAfter(accountConfirmation.getExpireAt())) {
            return new ResponseAPI<>(false,  AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED);
        }

        emailSender.send(prepareEmail(emailAddress, accountConfirmation.getId()));
        return new ResponseAPI<>(true, AccountConfirmationResendEmailResponseMessage.SUCCESS);
    }

    public void sendVerificationEmail(final User user) {
        final Instant emailExpireTime = Instant.now().plus(accountConfirmationProperties.getEmailExpireTime());
        final AccountConfirmation accountConfirmation = accountConfirmationRepository.save(new AccountConfirmation(user, emailExpireTime));

        emailSender.send(prepareEmail(user.getEmail(), accountConfirmation.getId()));
    }

    public ResponseAPI<AccountConfirmationResponseMessage> confirmAccount(final String confirmationToken) {
        final UUID token;
        try {
            token = UUID.fromString(confirmationToken);
        } catch (final IllegalArgumentException exception) {
            return new ResponseAPI<>(false, AccountConfirmationResponseMessage.INVALID_TOKEN);
        }

        final Optional<AccountConfirmation> emailOptional = accountConfirmationRepository.findById(token);
        if (emailOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResponseMessage.TOKEN_EXPIRED);
        }

        final AccountConfirmation accountConfirmation = emailOptional.get();
        final User user = accountConfirmation.getUser();
        user.setUserRole(UserRole.USER);
        accountConfirmationRepository.delete(accountConfirmation);

        return new ResponseAPI<>(true, AccountConfirmationResponseMessage.SUCCESS);
    }

    private EmailContent prepareEmail(final String emailAddress, final UUID token) {
        final String url = UriComponentsBuilder.fromUriString(accountConfirmationProperties.getReturnConfirmAddress())
                .queryParam("token", token)
                .build().toString();

        final String message = String.format(accountConfirmationProperties.getMessage(), url);

        return EmailContent.builder()
                .from(accountConfirmationProperties.getFrom())
                .to(emailAddress)
                .subject(accountConfirmationProperties.getMessageSubject())
                .message(message)
                .build();
    }

    @Scheduled(fixedRateString = "#{T(java.time.Duration).parse('${account.confirmation.email.config.cleanUpExpiredEmails}').toMillis()}")
    private void removeExpiredEmails() {
        accountConfirmationRepository.deleteAllByExpireAtBefore(Instant.now());
    }
}
