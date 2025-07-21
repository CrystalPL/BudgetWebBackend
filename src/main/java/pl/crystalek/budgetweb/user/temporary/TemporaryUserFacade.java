package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.user.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.user.auth.response.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.user.auth.response.RegisterResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TemporaryUserFacade {
    TemporaryUserRepository repository;
    CreateUser createUser;
    AccountVerificationEmailSender accountVerificationEmailSender;
    ConfirmAccount confirmAccount;

    public boolean existsByEmail(final String email) {
        return repository.existsByEmail(email);
    }

    public Optional<TemporaryUser> findByEmail(final String email) {
        return repository.findByEmail(email);
    }

    public RegisterResponse createUser(final RegisterRequest registerRequest) {
        return createUser.createUser(registerRequest);
    }

    public void sendVerificationEmail(final TemporaryUser user) {
        accountVerificationEmailSender.sendVerificationEmail(user);
    }

    public ResponseAPI<AccountConfirmationResendEmailResponseMessage> resendEmail(final String id) {
        return accountVerificationEmailSender.resendEmail(id);
    }

    public ResponseAPI<AccountConfirmationResponseMessage> confirmAccount(final String confirmationToken) {
        return confirmAccount.confirmAccount(confirmationToken);
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void removeExpiredEmails() {
        final Instant cutoffTime = Instant.now().minus(7, ChronoUnit.DAYS);
        repository.deleteAllByExpireAtBefore(cutoffTime);
    }
}
