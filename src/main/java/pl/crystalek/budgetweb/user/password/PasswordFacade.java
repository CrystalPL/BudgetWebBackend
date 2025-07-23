package pl.crystalek.budgetweb.user.password;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenService;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.password.request.PasswordResetRequest;
import pl.crystalek.budgetweb.user.password.response.PasswordRecoveryResponseMessage;
import pl.crystalek.budgetweb.user.password.response.PasswordResetResponseMessage;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class PasswordFacade {
    SendRecoveringEmail sendRecoveringEmail;
    ResetPassword resetPassword;
    ConfirmationTokenService confirmationTokenService;

    public ResponseAPI<PasswordRecoveryResponseMessage> sendRecoveringEmail(final String email) {
        return sendRecoveringEmail.sendRecoveringEmail(email);
    }

    public ResponseAPI<PasswordResetResponseMessage> resetPassword(final PasswordResetRequest passwordResetRequest) {
        return resetPassword.resetPassword(passwordResetRequest.token(), passwordResetRequest.password(), passwordResetRequest.confirmPassword());
    }

//    @Scheduled(fixedRateString = "#{T(java.time.Duration).parse('${password-recovery.email.config.cleanUpExpiredEmails}').toMillis()}")
//    private void removeExpiredEmails() {
//        confirmationTokenService.clearByExpireTime(ConfirmationTokenType.PASSWORD_RECOVERY);
//    }
}
