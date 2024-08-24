package pl.crystalek.budgetweb.user.account;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterRequest;
import pl.crystalek.budgetweb.user.User;
import pl.crystalek.budgetweb.user.UserRole;
import pl.crystalek.budgetweb.user.UserService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserAccountValidation {
    UserService userService;

    //ZWRACA OK GDY KONTO NIE ISTNIEJE
    public UserAccountValidationResult validateAccountExistsAndEnabled(final RegisterRequest registerRequest) {
        final Optional<User> userOptional = userService.getUserByEmail(registerRequest.email());
        if (userOptional.isPresent()) {
            if (userOptional.get().getUserRole() == UserRole.USER) {
                return UserAccountValidationResult.ACCOUNT_EXISTS;
            }

            return UserAccountValidationResult.ACCOUNT_NOT_ENABLED;
        }

        return UserAccountValidationResult.OK;
    }
}
