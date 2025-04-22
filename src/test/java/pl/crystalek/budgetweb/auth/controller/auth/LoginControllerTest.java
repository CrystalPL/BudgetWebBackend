package pl.crystalek.budgetweb.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.crystalek.budgetweb.auth.controller.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.LoginResponseMessage;
import pl.crystalek.budgetweb.auth.cookie.CookieService;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class LoginControllerTest extends BaseAccessControllerTest {
    ObjectMapper objectMapper;

    @Autowired
    public LoginControllerTest(final MockMvc mockMvc, final UserAccountUtil userAccountUtil, final ObjectMapper objectMapper) {
        super(mockMvc, userAccountUtil);
        this.objectMapper = objectMapper;
    }

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

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Required header 'User-Agent' is not present"));
    }

    @Test
    void shouldLoginFailWhenCredentialsAreValidAndAccountNotConfirmed() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.registrationToken").value(confirmationToken.toString()))
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.ACCOUNT_NOT_CONFIRMED.name()));
    }

    @Test
    void shouldLoginSuccessfullyWhenCredentialsAreValid() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", false);

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(confirmationToken.toString()))))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationToken").doesNotExist())
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.SUCCESS.name()));
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

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "InvalidPassword", true);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.registrationToken").doesNotExist())
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.BAD_CREDENTIALS.name()));
    }

    @Test
    void shouldReturnBadRequestWhenUserDoesNotExist() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "StrongPassword1!", true);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.registrationToken").doesNotExist())
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.USER_NOT_EXIST.name()));
    }

    @Test
    void shouldSetProperCookiesWhenLoginSuccessfulAndRememberMeTrue() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(confirmationToken.toString()))))
                .andExpect(status().isOk());

        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.SUCCESS.name()))
                .andExpect(cookie().exists("auth_token"))
                .andExpect(cookie().maxAge("auth_token", CookieService.COOKIE_MAX_AGE));
    }

    @Test
    void shouldSetProperCookiesWhenLoginSuccessfulAndRememberMeFalse() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(confirmationToken.toString()))))
                .andExpect(status().isOk());

        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.SUCCESS.name()))
                .andExpect(cookie().exists("auth_token"))
                .andExpect(cookie().maxAge("auth_token", -1));
    }
}