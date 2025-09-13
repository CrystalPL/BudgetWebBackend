package pl.crystalek.budgetweb.user.profile.password;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.profile.password.request.PasswordRecoveryRequest;
import pl.crystalek.budgetweb.user.profile.password.request.PasswordResetRequest;
import pl.crystalek.budgetweb.user.profile.password.response.PasswordRecoveryResponseMessage;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class PasswordController {
    PasswordFacade passwordFacade;

    @PostMapping("/recovery")
    public ResponseEntity<ResponseAPI<PasswordRecoveryResponseMessage>> passwordRecovery(@Valid @RequestBody final PasswordRecoveryRequest passwordRecoveryRequest) {
        final ResponseAPI<PasswordRecoveryResponseMessage> response = passwordFacade.sendRecoveringEmail(passwordRecoveryRequest.emailToReset());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponseAPI<?>> passwordReset(
            @Validated(PasswordResetRequest.PasswordResetRequestValidation.class)
            @RequestBody final PasswordResetRequest passwordResetRequest
    ) {
        final ResponseAPI<?> response = passwordFacade.resetPassword(passwordResetRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
