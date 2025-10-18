package pl.crystalek.budgetweb.user.profile.password.request;

import pl.crystalek.budgetweb.user.validator.email.ValidEmail;

public record PasswordRecoveryRequest(
        @ValidEmail
        String emailToReset
) {
}
