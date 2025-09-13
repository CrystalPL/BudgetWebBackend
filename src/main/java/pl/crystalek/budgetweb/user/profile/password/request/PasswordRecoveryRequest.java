package pl.crystalek.budgetweb.user.profile.password.request;

import pl.crystalek.budgetweb.share.validation.email.ValidEmail;

public record PasswordRecoveryRequest(
        @ValidEmail
        String emailToReset
) {
}
