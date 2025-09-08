package pl.crystalek.budgetweb.user.email;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.response.ConfirmEmailChangingResponseMessage;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ConfirmEmailChanging {
    ConfirmationTokenService confirmationTokenService;
    ChangeEmailRepository repository;

    ResponseAPI<ConfirmEmailChangingResponseMessage> confirmEmailChanging(final String changingToken) {
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

        return new ResponseAPI<>(true, ConfirmEmailChangingResponseMessage.SUCCESS);
    }
}
