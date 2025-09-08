package pl.crystalek.budgetweb.user.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.token.TokenFacade;
import pl.crystalek.budgetweb.token.model.AccessTokenDetails;
import pl.crystalek.budgetweb.user.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.user.auth.device.DeviceUtil;
import pl.crystalek.budgetweb.user.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.user.auth.request.LoginRequest;
import pl.crystalek.budgetweb.user.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.user.auth.request.ResendEmailRequest;
import pl.crystalek.budgetweb.user.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.user.auth.response.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.user.auth.response.LoginResponse;
import pl.crystalek.budgetweb.user.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.user.auth.response.RegisterResponse;
import pl.crystalek.budgetweb.user.temporary.RegistrationValidator;
import pl.crystalek.budgetweb.user.temporary.TemporaryUserFacade;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AuthController {
    AuthenticationService authenticationService;
    CookieService cookieService;
    TemporaryUserFacade temporaryUserFacade;
    TokenFacade tokenFacade;
    RegistrationValidator registrationValidator;

    @PostMapping("/login")
    public ResponseEntity<ResponseAPI<LoginResponseMessage>> login(
            @Validated(LoginRequest.LoginRequestValidation.class) @RequestBody final LoginRequest loginRequest,
            @RequestHeader("User-Agent") final String userAgent,
            final HttpServletResponse response) {
        final DeviceInfo deviceInfo = DeviceUtil.getDeviceInfo(userAgent);
        final LoginResponse responseAPI = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);

        return ResponseEntity.status(responseAPI.getStatusCode()).body(responseAPI);
    }

    @PostMapping("/verify")
    public void verify() {
    }

    @PostMapping("/resend-email")
    public ResponseEntity<ResponseAPI<AccountConfirmationResendEmailResponseMessage>> resendEmail(@Validated(ResendEmailRequest.ResendEmailValidation.class) @RequestBody final ResendEmailRequest resendEmailRequest) {
        final ResponseAPI<AccountConfirmationResendEmailResponseMessage> response = temporaryUserFacade.resendEmail(resendEmailRequest.registrationToken());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Validated(RegisterRequest.RegisterRequestValidation.class) @RequestBody final RegisterRequest registerRequest) {
        RegisterResponse result = registrationValidator.validate(registerRequest.email());
        if (result.isSuccess()) {
            result = temporaryUserFacade.createUser(registerRequest);
            if (result.isSuccess()) {
                temporaryUserFacade.sendVerificationEmail(result.getCreatedUser());
            }
        }

        return ResponseEntity.status(result.getStatusCode()).body(result);
    }

    @PostMapping("/confirm")
    public ResponseEntity<ResponseAPI<AccountConfirmationResponseMessage>> confirmAccountRegister(@Validated(AccountConfirmationRequest.AccountConfirmationValidation.class) @RequestBody final AccountConfirmationRequest accountConfirmationRequest, final HttpServletRequest request, final HttpServletResponse httpServletResponse) {
        final ResponseAPI<AccountConfirmationResponseMessage> response = temporaryUserFacade.confirmAccount(accountConfirmationRequest.confirmationToken());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public void logout(final HttpServletRequest request, final HttpServletResponse httpServletResponse) {
        final Optional<Cookie> cookieOptional = cookieService.getCookieWithToken(request.getCookies());
        final AccessTokenDetails accessTokenDetails = tokenFacade.decodeToken(cookieOptional.get().getValue());
        authenticationService.logout(accessTokenDetails.getRefreshTokenId());
        cookieService.deleteCookie(httpServletResponse);
    }
}
