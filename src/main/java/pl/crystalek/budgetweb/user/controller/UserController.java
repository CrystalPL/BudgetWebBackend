package pl.crystalek.budgetweb.user.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.controller.model.AccountInfoResponse;
import pl.crystalek.budgetweb.user.controller.model.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.controller.model.ChangeEmailResponseMessage;
import pl.crystalek.budgetweb.user.controller.model.ConfirmEmailChangingRequest;
import pl.crystalek.budgetweb.user.controller.model.ResendEmailChangingRequest;
import pl.crystalek.budgetweb.user.email.ChangeEmailService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class UserController {
    UserService userService;
    ChangeEmailService changeEmailService;

    @GetMapping("/info")
    private AccountInfoResponse getAccountInfo() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userService.getAccountInfo(userId);
    }

    @PostMapping("/change-email")
    private ResponseEntity<ResponseAPI<ChangeEmailResponseMessage>> changeEmail(@Valid @RequestBody final ChangeEmailRequest changeEmailRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final ResponseAPI<ChangeEmailResponseMessage> response = changeEmailService.changeEmail(userId, changeEmailRequest.email(), changeEmailRequest.password());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/change-password")
    private void changePassword() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }

    @PostMapping("/change-nickname")
    private void changeNickname() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }

    @PostMapping("/confirm-change-email")
    private void confirmChangeEmail(@RequestBody final ConfirmEmailChangingRequest confirmEmailChangingRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }

    @PostMapping("/resend-change-email")
    private void resendEmailChanging(@RequestBody final ResendEmailChangingRequest confirmEmailChangingRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
