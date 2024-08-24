package pl.crystalek.budgetweb.auth.passwordrecovery;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.crystalek.budgetweb.auth.controller.password.model.PasswordRecoveryResponseMessage;
import pl.crystalek.budgetweb.auth.controller.password.model.PasswordResetResponseMessage;
import pl.crystalek.budgetweb.auth.token.TokenService;
import pl.crystalek.budgetweb.email.EmailContent;
import pl.crystalek.budgetweb.email.EmailSender;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.User;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.password.PasswordValidation;
import pl.crystalek.budgetweb.user.password.PasswordValidationResult;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PasswordRecoveryService {
    PasswordRecoveryRepository repository;
    PasswordRecoveryProperties passwordRecoveryProperties;
    UserService userService;
    EmailSender emailSender;
    PasswordEncoder passwordEncoder;
    TokenService tokenService;

    public ResponseAPI<PasswordRecoveryResponseMessage> sendRecoveringEmail(final String email) {
        final Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, PasswordRecoveryResponseMessage.USER_NOT_FOUND);
        }

        final Instant emailExpireTime = Instant.now().plus(passwordRecoveryProperties.getEmailExpireTime());
        final PasswordRecovery passwordRecovery = repository.findByUser_Email(email).orElseGet(
                () -> repository.save(new PasswordRecovery(userOptional.get(), emailExpireTime))
        );

        emailSender.send(prepareEmail(email, passwordRecovery.getId()));
        return new ResponseAPI<>(true, PasswordRecoveryResponseMessage.SUCCESS);
    }

    public ResponseAPI<?> resetPassword(final String stringToken, final String password, final String confirmPassword) {
        final UUID token;
        try {
            token = UUID.fromString(stringToken);
        } catch (final IllegalArgumentException exception) {
            return new ResponseAPI<>(false, PasswordResetResponseMessage.INVALID_TOKEN);
        }

        final PasswordValidationResult passwordValidationResult = PasswordValidation.validatePassword(password);
        if (passwordValidationResult != PasswordValidationResult.OK) {
            return new ResponseAPI<>(false, passwordValidationResult);
        }

        final PasswordValidationResult confirmPasswordValidationResult = PasswordValidation.validatePassword(confirmPassword);
        if (confirmPasswordValidationResult != PasswordValidationResult.OK) {
            return new ResponseAPI<>(false, confirmPasswordValidationResult);
        }

        final Optional<PasswordRecovery> passwordRecoveryOptional = repository.findById(token);
        if (passwordRecoveryOptional.isEmpty()) {
            return new ResponseAPI<>(false, PasswordResetResponseMessage.TOKEN_EXPIRED);
        }

        if (!password.equals(confirmPassword)) {
            return new ResponseAPI<>(false, PasswordResetResponseMessage.PASSWORD_MISMATCH);
        }

        final PasswordRecovery passwordRecovery = passwordRecoveryOptional.get();
        final User user = passwordRecovery.getUser();
        user.setPassword(passwordEncoder.encode(password));
        repository.delete(passwordRecovery);
        tokenService.logoutUserFromDevices(user);

        return new ResponseAPI<>(true, PasswordResetResponseMessage.SUCCESS);
    }

    private EmailContent prepareEmail(final String emailAddress, final UUID token) {
        final String url = UriComponentsBuilder.fromUriString(passwordRecoveryProperties.getReturnConfirmAddress())
                .queryParam("token", token)
                .build().toString();

        final String message = String.format(passwordRecoveryProperties.getMessage(), url);

        return EmailContent.builder()
                .from(passwordRecoveryProperties.getFrom())
                .to(emailAddress)
                .subject(passwordRecoveryProperties.getMessageSubject())
                .message(message)
                .build();
    }

    @Scheduled(fixedRateString = "#{T(java.time.Duration).parse('${password-recovery.email.config.cleanUpExpiredEmails}').toMillis()}")
    private void removeExpiredEmails() {
        repository.deleteAllByExpireAtBefore(Instant.now());
    }
}
