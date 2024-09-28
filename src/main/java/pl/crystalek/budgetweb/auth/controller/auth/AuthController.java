package pl.crystalek.budgetweb.auth.controller.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.auth.AuthenticationService;
import pl.crystalek.budgetweb.auth.confirmation.AccountConfirmationService;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.model.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.LoginResponseMessage;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterResponse;
import pl.crystalek.budgetweb.auth.cookie.CookieService;
import pl.crystalek.budgetweb.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.auth.device.DeviceUtil;
import pl.crystalek.budgetweb.auth.token.TokenCreator;
import pl.crystalek.budgetweb.auth.token.TokenDecoder;
import pl.crystalek.budgetweb.auth.token.model.AccessTokenDetails;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserRole;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.UserValidator;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AuthController {
    AuthenticationService authenticationService;
    UserService userService;
    AccountConfirmationService accountConfirmationService;
    CookieService cookieService;
    TokenDecoder tokenDecoder;
    TokenCreator tokenCreator;
    UserValidator userValidator;

    @PostMapping("/login")
    private ResponseEntity<ResponseAPI<LoginResponseMessage>> login(@Valid @RequestBody final LoginRequest loginRequest, final HttpServletRequest request, final HttpServletResponse response) {
        final String userAgent = request.getHeader("User-Agent");
        final DeviceInfo deviceInfo = DeviceUtil.getDeviceInfo(userAgent);
        final ResponseAPI<LoginResponseMessage> responseAPI = authenticationService.authenticateAndAddCookie(loginRequest, response, deviceInfo);

        return ResponseEntity.status(responseAPI.getStatusCode()).body(responseAPI);
    }

    @PostMapping("/verify")
    private void verify() {
    }

    @PostMapping("/resend-email")
    private ResponseEntity<ResponseAPI<AccountConfirmationResendEmailResponseMessage>> resendEmail() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Optional<String> emailOptional = userService.getEmailByUserId(userId);
        final String emailAddress = emailOptional.get(); //ignoruje optionala, bo został sprawdzony w AuthenticationFilter

        final ResponseAPI<AccountConfirmationResendEmailResponseMessage> response = accountConfirmationService.resendEmail(emailAddress);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/register")
    private ResponseEntity<RegisterResponse> register(@Valid @RequestBody final RegisterRequest registerRequest) {
        RegisterResponse result = userValidator.validate(registerRequest.email());
        if (result.isSuccess()) {
            result = userService.createUser(registerRequest);
            if (result.isSuccess()) {
                accountConfirmationService.sendVerificationEmail(result.getCreatedUser());
            }
        }


        return ResponseEntity.status(result.getStatusCode()).body(result);
    }

    @PostMapping("/confirm")
    private ResponseEntity<ResponseAPI<AccountConfirmationResponseMessage>> confirmAccountRegister(@Valid @RequestBody final AccountConfirmationRequest accountConfirmationRequest, final HttpServletRequest request, final HttpServletResponse httpServletResponse) {
        final ResponseAPI<AccountConfirmationResponseMessage> response = accountConfirmationService.confirmAccount(accountConfirmationRequest.confirmationToken());
        if (!response.isSuccess()) {
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }

        final Optional<Cookie> cookieOptional = cookieService.getCookieWithToken(request.getCookies());
        if (cookieOptional.isPresent()) {
            final Cookie cookie = cookieOptional.get();
            final AccessTokenDetails accessTokenDetails = tokenDecoder.decodeToken(cookie.getValue());
            if (!accessTokenDetails.isExpired() && accessTokenDetails.isVerified()) {
                final String accessToken = tokenCreator.createWithExpires(accessTokenDetails.getUserId(), accessTokenDetails.getRefreshTokenId(), UserRole.USER, accessTokenDetails.getExpiresAt());
                cookieService.createCookieAndAddToResponse(accessToken, cookie.getMaxAge() != -1, httpServletResponse);
            }
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
