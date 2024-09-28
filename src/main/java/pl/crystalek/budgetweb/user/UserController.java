package pl.crystalek.budgetweb.user;

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
import pl.crystalek.budgetweb.user.email.ChangeEmailService;
import pl.crystalek.budgetweb.user.model.AccountInfoResponse;
import pl.crystalek.budgetweb.user.model.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.model.ChangeEmailResponseMessage;
import pl.crystalek.budgetweb.user.model.ChangeNicknameRequest;
import pl.crystalek.budgetweb.user.model.ChangeNicknameResponseMessage;
import pl.crystalek.budgetweb.user.model.ChangePasswordRequest;
import pl.crystalek.budgetweb.user.model.ChangePasswordResponseMessage;
import pl.crystalek.budgetweb.user.model.ConfirmEmailChangingRequest;
import pl.crystalek.budgetweb.user.model.ConfirmEmailChangingResponseMessage;
import pl.crystalek.budgetweb.user.model.GetEmailChangingInfoResponse;

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
    private ResponseEntity<ResponseAPI<ChangePasswordResponseMessage>> changePassword(@Valid @RequestBody final ChangePasswordRequest changePasswordRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final ResponseAPI<ChangePasswordResponseMessage> response = userService.changePassword(userId, changePasswordRequest.oldPassword(), changePasswordRequest.password());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/change-nickname")
    private ResponseEntity<ResponseAPI<ChangeNicknameResponseMessage>> changeNickname(@Valid @RequestBody final ChangeNicknameRequest changeNicknameRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final ResponseAPI<ChangeNicknameResponseMessage> response = userService.changeNickname(userId, changeNicknameRequest.nickname());
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PostMapping("/confirm-change-email")
    private ResponseEntity<ResponseAPI<ConfirmEmailChangingResponseMessage>> confirmChangeEmail(@Valid @RequestBody final ConfirmEmailChangingRequest confirmEmailChangingRequest) {
        final ResponseAPI<ConfirmEmailChangingResponseMessage> response = changeEmailService.confirmEmailChanging(confirmEmailChangingRequest.token());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/email-changing-wait-to-confirm")
    private GetEmailChangingInfoResponse isEmailChangingWaitingToConfirm() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new GetEmailChangingInfoResponse(changeEmailService.isEmailChangingWaitingToConfirm(userId));
    }
}
