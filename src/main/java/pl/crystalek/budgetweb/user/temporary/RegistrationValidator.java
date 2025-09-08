package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.auth.response.RegisterResponse;
import pl.crystalek.budgetweb.user.auth.response.RegisterResponseMessage;
import pl.crystalek.budgetweb.user.email.ChangeEmailFacade;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationValidator {
    UserService userService;
    ChangeEmailFacade changeEmailFacade;
    TemporaryUserFacade temporaryUserFacade;

    public RegisterResponse validate(final String email) {
        if (changeEmailFacade.isEmailExists(email)) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_EXISTS);
        }

        if (userService.isUserExists(email)) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_EXISTS);
        }

        if (temporaryUserFacade.existsByEmail(email)) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_NOT_ENABLED);
        }

        return new RegisterResponse(true, RegisterResponseMessage.SUCCESS);
    }
}
