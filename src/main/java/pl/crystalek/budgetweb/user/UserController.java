package pl.crystalek.budgetweb.user;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.email.ChangeEmailService;
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.request.ChangeNicknameRequest;
import pl.crystalek.budgetweb.user.request.ChangePasswordRequest;
import pl.crystalek.budgetweb.user.request.ConfirmEmailChangingRequest;
import pl.crystalek.budgetweb.user.response.AccountInfoResponse;
import pl.crystalek.budgetweb.user.response.ChangeEmailResponseMessage;
import pl.crystalek.budgetweb.user.response.ChangeNicknameResponseMessage;
import pl.crystalek.budgetweb.user.response.ChangePasswordResponseMessage;
import pl.crystalek.budgetweb.user.response.ConfirmEmailChangingResponseMessage;
import pl.crystalek.budgetweb.user.response.GetEmailChangingInfoResponse;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class UserController {
    UserService userService;
    ChangeEmailService changeEmailService;

    @GetMapping("/info")
    private AccountInfoResponse getAccountInfo(@AuthenticationPrincipal final long userId) {
        return userService.getAccountInfo(userId);
    }

    @PostMapping("/change-email")
    private ResponseEntity<ResponseAPI<ChangeEmailResponseMessage>> changeEmail(
            @Valid @RequestBody final ChangeEmailRequest changeEmailRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<ChangeEmailResponseMessage> response = changeEmailService.changeEmail(userId, changeEmailRequest.email(), changeEmailRequest.password());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/change-password")
    private ResponseEntity<ResponseAPI<ChangePasswordResponseMessage>> changePassword(
            @Valid @RequestBody final ChangePasswordRequest changePasswordRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<ChangePasswordResponseMessage> response = userService.changePassword(userId, changePasswordRequest.oldPassword(), changePasswordRequest.password());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/change-nickname")
    private ResponseEntity<ResponseAPI<ChangeNicknameResponseMessage>> changeNickname(
            @Valid @RequestBody final ChangeNicknameRequest changeNicknameRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<ChangeNicknameResponseMessage> response = userService.changeNickname(userId, changeNicknameRequest.nickname());
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PostMapping("/confirm-change-email")
    private ResponseEntity<ResponseAPI<ConfirmEmailChangingResponseMessage>> confirmChangeEmail(@Valid @RequestBody final ConfirmEmailChangingRequest confirmEmailChangingRequest) {
        final ResponseAPI<ConfirmEmailChangingResponseMessage> response = changeEmailService.confirmEmailChanging(confirmEmailChangingRequest.token());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/email-changing-wait-to-confirm")
    private GetEmailChangingInfoResponse isEmailChangingWaitingToConfirm(@AuthenticationPrincipal final long userId) {
        return new GetEmailChangingInfoResponse(changeEmailService.isEmailChangingWaitingToConfirm(userId));
    }
}
