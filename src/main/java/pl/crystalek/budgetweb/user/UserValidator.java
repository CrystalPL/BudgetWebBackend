package pl.crystalek.budgetweb.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterResponse;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterResponseMessage;
import pl.crystalek.budgetweb.user.email.ChangeEmailService;
import pl.crystalek.budgetweb.user.model.UserDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserValidator { //klasa utworzona, aby uniknąć circular dependency
    UserService userService;
    ChangeEmailService changeEmailService;

    public RegisterResponse validate(final String email) {
        if (changeEmailService.isEmailExists(email)) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_EXISTS);
        }

        final Optional<UserDTO> userOptional = userService.getUserDTO(email);
        if (userOptional.isEmpty()) {
            return new RegisterResponse(true, RegisterResponseMessage.SUCCESS);
        }

        if (userOptional.get().userRole() == UserRole.USER) {
            return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_EXISTS);
        }

        return new RegisterResponse(false, RegisterResponseMessage.ACCOUNT_NOT_ENABLED);
    }


}
