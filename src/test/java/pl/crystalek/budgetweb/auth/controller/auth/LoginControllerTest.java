package pl.crystalek.budgetweb.auth.controller.auth;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.auth.cookie.CookieService;
import pl.crystalek.budgetweb.auth.token.TokenProperties;
import pl.crystalek.budgetweb.auth.token.model.RefreshToken;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class LoginControllerTest extends BaseAccessControllerTest {
    TokenProperties tokenProperties;

    @Override
    protected String[][] shouldAllowAccessWithoutAccount() {
        return new String[][]{{"/auth/login", "POST"}};
    }

    @Override
    protected String[][] shouldDeniedAccessWithAccount() {
        return new String[][]{{"/auth/login", "POST"}};
    }

    @Test
    void shouldLoginFailWhenUserAgentIsAbsent() throws Exception {
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .headers(Map.of())
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseObject("Required header 'User-Agent' is not present")
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldLoginFailWhenCredentialsAreValidAndAccountNotConfirmed() throws Exception {
        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);

        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(LoginResponseMessage.ACCOUNT_NOT_CONFIRMED)
                .expect(jsonPath("$.registrationToken").value(confirmationToken.toString()))
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldLoginSuccessfullyWhenCredentialsAreValid() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", false);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expect(jsonPath("$.registrationToken").doesNotExist())
                .expectedResponseMessage(LoginResponseMessage.SUCCESS)
                .expectedResponseCode(HttpStatus.OK)
                .build()
                .sendRequest(mockMvc);

        final List<RefreshToken> tokenList = entityManager.createQuery("select rt from RefreshToken rt", RefreshToken.class).getResultList();
        final Long userId = entityManager.createQuery("select u.id from User u", Long.class).getResultList().getFirst();

        assertEquals(1, tokenList.size());
        assertEquals("Windows 10", tokenList.getFirst().getDeviceInfo().OSName());
        assertEquals("Chrome", tokenList.getFirst().getDeviceInfo().browserName());
        assertEquals(userId, tokenList.getFirst().getUser().getId());
        assertFalse(tokenList.getFirst().isRememberMe());
        assertThat(tokenList.getFirst().getExpireAt()).isBetween(
                Instant.now(),
                Instant.now().plus(Duration.ofMinutes(tokenProperties.getRefreshTokenExpireTime().plus(Duration.ofMinutes(1)).toMinutes()))
        );

    }

    @ParameterizedTest
    @CsvSource({
            "'', , , 'MISSING_EMAIL'",
            ", , , 'MISSING_EMAIL'",
            "'test@example.com', '', , 'MISSING_PASSWORD'",
            "'test@example.com', , , 'MISSING_PASSWORD'",
            "'test@example.com', 'StrongPassword1!', , 'MISSING_REMEMBER_ME'",
    })
    void shouldFailValidation(final String email, final String password, final Boolean rememberMe, final String errorMessage) throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final LoginRequest loginRequest = new LoginRequest(email, password, rememberMe);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "InvalidPassword", true);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expect(jsonPath("$.registrationToken").doesNotExist())
                .expectedResponseMessage(LoginResponseMessage.BAD_CREDENTIALS)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldReturnBadRequestWhenUserDoesNotExist() throws Exception {
        final LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "StrongPassword1!", true);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expect(jsonPath("$.registrationToken").doesNotExist())
                .expectedResponseMessage(LoginResponseMessage.USER_NOT_EXIST)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldSetProperCookiesWhenLoginSuccessfulAndRememberMeTrue() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);

        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.OK)
                .expect(jsonPath("$.registrationToken").doesNotExist())
                .expectedResponseMessage(LoginResponseMessage.SUCCESS)
                .expect(cookie().exists(tokenProperties.getCookieName()))
                .expect(cookie().maxAge(tokenProperties.getCookieName(), CookieService.COOKIE_MAX_AGE))
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldSetProperCookiesWhenLoginSuccessfulAndRememberMeFalse() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);

        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", false);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(LoginResponseMessage.SUCCESS)
                .expect(cookie().exists(tokenProperties.getCookieName()))
                .expect(cookie().maxAge(tokenProperties.getCookieName(), -1))
                .build()
                .sendRequest(mockMvc);
    }
}