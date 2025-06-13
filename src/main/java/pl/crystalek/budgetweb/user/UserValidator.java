package pl.crystalek.budgetweb.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.response.RegisterResponse;
import pl.crystalek.budgetweb.auth.controller.auth.response.RegisterResponseMessage;
import pl.crystalek.budgetweb.user.email.ChangeEmailService;
import pl.crystalek.budgetweb.user.temporary.TemporaryUserService;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserValidator { //klasa utworzona, aby uniknąć circular dependency
    UserService userService;
    ChangeEmailService changeEmailService;
    TemporaryUserService temporaryUserService;

    public RegisterResponse validate(final String email) {
        if (changeEmailService.isEmailExists(email)) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_EXISTS);
        }

        if (userService.isUserExists(email)) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_EXISTS);
        }

        if (temporaryUserService.existsByEmail(email)) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_NOT_ENABLED);
        }

        return new RegisterResponse(true, RegisterResponseMessage.SUCCESS);
    }
}
