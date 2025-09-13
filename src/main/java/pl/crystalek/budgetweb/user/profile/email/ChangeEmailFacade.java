package pl.crystalek.budgetweb.user.profile.email;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.response.ChangeEmailResponseMessage;
import pl.crystalek.budgetweb.user.response.ConfirmEmailChangingResponseMessage;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChangeEmailFacade {
    SendChangeEmail sendChangeEmail;
    ConfirmEmailChanging confirmEmailChanging;
    ChangeEmailRepository repository;

    public boolean isEmailExists(final String email) {
        return repository.existsByNewEmail(email);
    }

    public boolean isEmailChangingWaitingToConfirm(final long userId) {
        return repository.existsByConfirmationToken_User_Id(userId);
    }

    public ResponseAPI<ChangeEmailResponseMessage> changeEmail(final long userId, final ChangeEmailRequest changeEmailRequest) {
        return sendChangeEmail.changeEmail(userId, changeEmailRequest);
    }

    public ResponseAPI<ConfirmEmailChangingResponseMessage> confirmEmailChanging(final String changingToken) {
        return confirmEmailChanging.confirmEmailChanging(changingToken);
    }

}
