package pl.crystalek.budgetweb.user.profile.email;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.request.ConfirmEmailChangingRequest;
import pl.crystalek.budgetweb.user.response.ChangeEmailResponseMessage;
import pl.crystalek.budgetweb.user.response.ConfirmEmailChangingResponseMessage;
import pl.crystalek.budgetweb.user.response.GetEmailChangingInfoResponse;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ChangeEmailController {
    ChangeEmailFacade changeEmailFacade;

    @PostMapping("/change-email")
    private ResponseEntity<ResponseAPI<ChangeEmailResponseMessage>> changeEmail(
            @Validated(ChangeEmailRequest.ChangeEmailRequestValidation.class) @RequestBody final ChangeEmailRequest changeEmailRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<ChangeEmailResponseMessage> response = changeEmailFacade.changeEmail(userId, changeEmailRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/confirm-change-email")
    private ResponseEntity<ResponseAPI<ConfirmEmailChangingResponseMessage>> confirmChangeEmail(
            @Validated(ConfirmEmailChangingRequest.ConfirmEmailChangingRequestValidation.class)
            @RequestBody final ConfirmEmailChangingRequest confirmEmailChangingRequest
    ) {
        final ResponseAPI<ConfirmEmailChangingResponseMessage> response = changeEmailFacade.confirmEmailChanging(confirmEmailChangingRequest.token());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/email-changing-wait-to-confirm")
    private GetEmailChangingInfoResponse isEmailChangingWaitingToConfirm(@AuthenticationPrincipal final long userId) {
        return new GetEmailChangingInfoResponse(changeEmailFacade.isEmailChangingWaitingToConfirm(userId));
    }
}
