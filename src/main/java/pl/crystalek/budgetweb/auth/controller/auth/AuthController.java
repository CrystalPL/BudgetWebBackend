package pl.crystalek.budgetweb.auth.controller.auth;

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
import pl.crystalek.budgetweb.auth.AuthenticationService;
import pl.crystalek.budgetweb.auth.controller.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.ResendEmailRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.response.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.response.LoginResponse;
import pl.crystalek.budgetweb.auth.controller.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.response.RegisterResponse;
import pl.crystalek.budgetweb.auth.cookie.CookieService;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.device.DeviceUtil;
import pl.crystalek.budgetweb.auth.token.TokenDecoder;
import pl.crystalek.budgetweb.auth.token.model.AccessTokenDetails;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserValidator;
import pl.crystalek.budgetweb.user.temporary.TemporaryUserService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AuthController {
    AuthenticationService authenticationService;
    CookieService cookieService;
    TemporaryUserService temporaryUserService;
    TokenDecoder tokenDecoder;
    UserValidator userValidator;

    @PostMapping("/login")
    public ResponseEntity<ResponseAPI<LoginResponseMessage>> login(@Validated(LoginRequest.LoginRequestValidation.class) @RequestBody final LoginRequest loginRequest, @RequestHeader("User-Agent") String userAgent, final HttpServletResponse response) {
        final DeviceInfo deviceInfo = DeviceUtil.getDeviceInfo(userAgent);
        final LoginResponse responseAPI = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);

        return ResponseEntity.status(responseAPI.getStatusCode()).body(responseAPI);
    }

    @PostMapping("/verify")
    public void verify() {
    }

    @PostMapping("/resend-email")
    public ResponseEntity<ResponseAPI<AccountConfirmationResendEmailResponseMessage>> resendEmail(@Validated(ResendEmailRequest.ResendEmailValidation.class) @RequestBody final ResendEmailRequest resendEmailRequest) {
        final ResponseAPI<AccountConfirmationResendEmailResponseMessage> response = temporaryUserService.resendEmail(resendEmailRequest.registrationToken());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Validated(RegisterRequest.RegisterRequestValidation.class) @RequestBody final RegisterRequest registerRequest) {
        RegisterResponse result = userValidator.validate(registerRequest.email());
        if (result.isSuccess()) {
            result = temporaryUserService.createUser(registerRequest);
            if (result.isSuccess()) {
                temporaryUserService.sendVerificationEmail(result.getCreatedUser());
            }
        }

        return ResponseEntity.status(result.getStatusCode()).body(result);
    }

    @PostMapping("/confirm")
    public ResponseEntity<ResponseAPI<AccountConfirmationResponseMessage>> confirmAccountRegister(@Validated(AccountConfirmationRequest.AccountConfirmationValidation.class) @RequestBody final AccountConfirmationRequest accountConfirmationRequest, final HttpServletRequest request, final HttpServletResponse httpServletResponse) {
        final ResponseAPI<AccountConfirmationResponseMessage> response = temporaryUserService.confirmAccount(accountConfirmationRequest.confirmationToken());

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public void logout(final HttpServletRequest request, final HttpServletResponse httpServletResponse) {
        final Optional<Cookie> cookieOptional = cookieService.getCookieWithToken(request.getCookies()); //nie sprawdzam czy optional jest pusty, bo zrobił to authenticationfilter
        final AccessTokenDetails accessTokenDetails = tokenDecoder.decodeToken(cookieOptional.get().getValue());
        authenticationService.logout(accessTokenDetails.getRefreshTokenId());
        cookieService.deleteCookie(httpServletResponse);
    }
}
