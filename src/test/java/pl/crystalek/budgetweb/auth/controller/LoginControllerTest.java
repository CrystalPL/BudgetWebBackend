package pl.crystalek.budgetweb.auth.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.auth.CookieService;
import pl.crystalek.budgetweb.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.helper.BaseAccessControllerTest;
import pl.crystalek.budgetweb.helper.UserAccountUtil;
import pl.crystalek.budgetweb.helper.request.RequestHelper;
import pl.crystalek.budgetweb.token.TokenProperties;
import pl.crystalek.budgetweb.token.model.RefreshToken;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldLoginFailWhenUserAgentIsAbsent() {
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
    void shouldLoginFailWhenCredentialsAreValidAndAccountNotConfirmed() {
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

    @ParameterizedTest
    @CsvSource({
            "false, false",
            "true, true"
    })
    void shouldLoginSuccessfullyWhenCredentialsAreValid(boolean rememberMe, boolean expectedRememberMeFlag) {
        userAccountUtil.registerDefaultUser();
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", rememberMe);

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
        assertEquals(expectedRememberMeFlag, tokenList.getFirst().isRememberMe());
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
    void shouldFailValidationWhenInputDataIsInvalid(final String email, final String password, final Boolean rememberMe, final String errorMessage) {
        userAccountUtil.registerDefaultUser();
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

    @ParameterizedTest
    @CsvSource({
            "test@example.com, InvalidPassword, BAD_CREDENTIALS, true",
            "nonexistent@example.com, StrongPassword1!, USER_NOT_EXIST, false"
    })
    void shouldReturnBadRequestForInvalidLoginScenarios(String email, String password, LoginResponseMessage errorMessage, boolean userExists) {
        if (userExists) {
            userAccountUtil.registerDefaultUser();
        }

        final LoginRequest loginRequest = new LoginRequest(email, password, true);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expect(jsonPath("$.registrationToken").doesNotExist())
                .expectedResponseMessage(errorMessage)
                .build()
                .sendRequest(mockMvc);
    }

    @ParameterizedTest
    @CsvSource({
            "true, " + CookieService.COOKIE_MAX_AGE,
            "false, -1"
    })
    void shouldSetProperCookiesWhenLoginSuccessful(boolean rememberMe, int expectedMaxAge) {
        userAccountUtil.registerDefaultUser();
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", rememberMe);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.OK)
                .expect(jsonPath("$.registrationToken").doesNotExist())
                .expectedResponseMessage(LoginResponseMessage.SUCCESS)
                .expect(cookie().exists(tokenProperties.getCookieName()))
                .expect(cookie().maxAge(tokenProperties.getCookieName(), expectedMaxAge))
                .build()
                .sendRequest(mockMvc);
    }
}