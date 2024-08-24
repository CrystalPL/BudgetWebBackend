package pl.crystalek.budgetweb.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.account.UserAccountValidation;
import pl.crystalek.budgetweb.user.account.UserAccountValidationResult;
import pl.crystalek.budgetweb.user.data.UserDataValidation;
import pl.crystalek.budgetweb.user.data.UserDataValidationResult;
import pl.crystalek.budgetweb.user.password.PasswordValidation;
import pl.crystalek.budgetweb.user.password.PasswordValidationResult;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserValidator {
    UserAccountValidation userAccountValidation;

    public ResponseAPI<?> validateUser(final RegisterRequest registerRequest) {
        final UserDataValidationResult userDataValidationResult = UserDataValidation.validateUserData(registerRequest);
        if (userDataValidationResult != UserDataValidationResult.OK) {
            return new ResponseAPI<>(false, userDataValidationResult);
        }

        final PasswordValidationResult passwordValidationResult = PasswordValidation.validatePassword(registerRequest.password());
        if (passwordValidationResult != PasswordValidationResult.OK) {
            return new ResponseAPI<>(false, passwordValidationResult);
        }

        final UserAccountValidationResult userAccountValidationResult = userAccountValidation.validateAccountExistsAndEnabled(registerRequest);
        if (userAccountValidationResult != UserAccountValidationResult.OK) {
            return new ResponseAPI<>(false, userAccountValidationResult);
        }

        return new ResponseAPI<>(true, RegisterResponseMessage.SUCCESS);
    }

}
