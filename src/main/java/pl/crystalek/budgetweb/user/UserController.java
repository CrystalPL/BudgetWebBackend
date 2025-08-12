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
import pl.crystalek.budgetweb.user.request.ChangeNicknameRequest;
import pl.crystalek.budgetweb.user.request.ChangePasswordRequest;
import pl.crystalek.budgetweb.user.response.AccountInfoResponse;
import pl.crystalek.budgetweb.user.response.ChangeNicknameResponseMessage;
import pl.crystalek.budgetweb.user.response.ChangePasswordResponseMessage;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class UserController {
    UserService userService;

    @GetMapping("/info")
    private AccountInfoResponse getAccountInfo(@AuthenticationPrincipal final long userId) {
        return userService.getAccountInfo(userId);
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
}
