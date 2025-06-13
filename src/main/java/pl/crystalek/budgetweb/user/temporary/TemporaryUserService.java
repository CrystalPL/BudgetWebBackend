package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.response.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.response.RegisterResponse;
import pl.crystalek.budgetweb.auth.controller.auth.response.RegisterResponseMessage;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TemporaryUserService {
    TemporaryUserRepository repository;
    PasswordEncoder passwordEncoder;
    AccountConfirmationProperties accountConfirmationProperties;
    EmailSender emailSender;
    UserService userService;

    public boolean existsByEmail(final String email) {
        return repository.existsByEmail(email);
    }

    public Optional<TemporaryUser> findByEmail(final String email) {
        return repository.findByEmail(email);
    }

    public RegisterResponse createUser(final RegisterRequest registerRequest) {
        final Instant emailExpireTime = Instant.now().plus(accountConfirmationProperties.getEmailExpireTime());

        final TemporaryUser user = new TemporaryUser(registerRequest.email(), passwordEncoder.encode(registerRequest.password()), registerRequest.username(), registerRequest.receiveUpdates(), emailExpireTime);
        return new RegisterResponse(true, RegisterResponseMessage.SUCCESS, repository.save(user));
    }

    public void sendVerificationEmail(final TemporaryUser user) {
        final EmailContent emailContent = EmailContent.ofBasicEmail(accountConfirmationProperties, user.getEmail(), user.getId().toString());
        emailSender.send(emailContent);
    }

    public ResponseAPI<AccountConfirmationResendEmailResponseMessage> resendEmail(final String id) {
        final UUID uuid = UUID.fromString(id);
        final Optional<TemporaryUser> userOptional = repository.findById(uuid);
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED);
        }

        final TemporaryUser user = userOptional.get();
        if (Instant.now().isAfter(user.getExpireAt())) {
            return new ResponseAPI<>(false, AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED);
        }

        final EmailContent emailContent = EmailContent.ofBasicEmail(accountConfirmationProperties, user.getEmail(), user.getId().toString());
        emailSender.send(emailContent);
        return new ResponseAPI<>(true, AccountConfirmationResendEmailResponseMessage.SUCCESS);
    }

    public ResponseAPI<AccountConfirmationResponseMessage> confirmAccount(final String confirmationToken) {
        final UUID uuid = UUID.fromString(confirmationToken);
        final Optional<TemporaryUser> userOptional = repository.findById(uuid);
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResponseMessage.TOKEN_EXPIRED);
        }

        final TemporaryUser user = userOptional.get();
        userService.createUser(user);

        repository.delete(user);
        return new ResponseAPI<>(true, AccountConfirmationResponseMessage.SUCCESS);
    }

    //TODO AUTOMATYCZNE CZYSZCZENIE UZYTKOWNIKOW, KTORZY W CIAGU NP 14 DNI NIE POTWIERDZILI REJESTRACJI SWOJEGO KONTA
}
