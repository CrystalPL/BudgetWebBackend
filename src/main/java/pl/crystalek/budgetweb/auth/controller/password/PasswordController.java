package pl.crystalek.budgetweb.auth.controller.password;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.auth.controller.password.model.PasswordRecoveryRequest;
import pl.crystalek.budgetweb.auth.controller.password.model.PasswordRecoveryResponseMessage;
import pl.crystalek.budgetweb.auth.controller.password.model.PasswordResetRequest;
import pl.crystalek.budgetweb.auth.passwordrecovery.PasswordRecoveryService;
import pl.crystalek.budgetweb.share.ResponseAPI;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class PasswordController {
    PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/recovery")
    private ResponseEntity<ResponseAPI<PasswordRecoveryResponseMessage>> passwordRecovery(@Valid @RequestBody final PasswordRecoveryRequest passwordRecoveryRequest) {
        final ResponseAPI<PasswordRecoveryResponseMessage> response = passwordRecoveryService.sendRecoveringEmail(passwordRecoveryRequest.emailToReset());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset")
    private ResponseEntity<ResponseAPI<?>> passwordReset(@Valid @RequestBody final PasswordResetRequest passwordResetRequest) {
        final ResponseAPI<?> response = passwordRecoveryService.resetPassword(passwordResetRequest.token(), passwordResetRequest.password(), passwordResetRequest.confirmPassword());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
