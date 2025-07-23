package pl.crystalek.budgetweb.user.password;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.token.TokenFacade;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.password.response.PasswordResetResponseMessage;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ResetPassword {
    ConfirmationTokenService confirmationTokenService;
    TokenFacade tokenFacade;
    PasswordEncoder passwordEncoder;

    ResponseAPI<PasswordResetResponseMessage> resetPassword(final String stringToken, final String password, final String confirmPassword) {
        final UUID token = UUID.fromString(stringToken);

        final Optional<ConfirmationToken> tokenOptional = confirmationTokenService.getConfirmationToken(token, ConfirmationTokenType.PASSWORD_RECOVERY);
        if (tokenOptional.isEmpty()) {
            return new ResponseAPI<>(false, PasswordResetResponseMessage.TOKEN_EXPIRED);
        }

        final ConfirmationToken passwordRecovery = tokenOptional.get();
        resetPassword(password, passwordRecovery);

        return new ResponseAPI<>(true, PasswordResetResponseMessage.SUCCESS);
    }

    private void resetPassword(final String password, final ConfirmationToken passwordRecovery) {
        final User user = passwordRecovery.getUser();
        user.setPassword(passwordEncoder.encode(password));
        confirmationTokenService.delete(passwordRecovery);
        tokenFacade.logoutUserFromDevices(user);
    }
}
