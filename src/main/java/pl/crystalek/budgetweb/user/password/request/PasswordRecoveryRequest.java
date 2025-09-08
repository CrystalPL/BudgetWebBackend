package pl.crystalek.budgetweb.user.password.request;

import pl.crystalek.budgetweb.share.validation.email.ValidEmail;

public record PasswordRecoveryRequest(
        @ValidEmail
        String emailToReset
) {
}
