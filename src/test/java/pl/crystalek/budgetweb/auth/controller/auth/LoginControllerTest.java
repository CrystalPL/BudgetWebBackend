package pl.crystalek.budgetweb.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.LoginResponseMessage;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.crystalek.budgetweb.utils.UserAccountUtil.getGuidFromByteArray;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class LoginControllerTest extends BaseAccessControllerTest {
    ObjectMapper objectMapper;
    JdbcTemplate jdbcTemplate;
    EntityManager entityManager;

    @Autowired
    public LoginControllerTest(final MockMvc mockMvc, final UserAccountUtil userAccountUtil, final ObjectMapper objectMapper, final JdbcTemplate jdbcTemplate, final EntityManager entityManager) {
        super(mockMvc, userAccountUtil);
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Override
    protected String[][] shouldAllowAccessWithoutAccount() {
        return new String[][]{{"/auth/login", "POST"}};
    }

    @Override
    protected String[][] shouldDeniedAccessWithGuestRole() {
        return new String[][]{{"/auth/login", "POST"}};
    }

    @Override
    protected String[][] shouldDeniedAccessWithUserRole() {
        return new String[][]{{"/auth/login", "POST"}};
    }

    @Test
    void shouldLoginFailWhenCredentialsAreValidAndAccountNotConfirmed() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.ACCOUNT_NOT_CONFIRMED.name()));
    }

    @Test
    void shouldLoginSuccessfullyWhenCredentialsAreValid() throws Exception {
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        entityManager.flush();
        final byte[] token = jdbcTemplate.queryForObject("SELECT id from confirmation_token", byte[].class);
        final UUID uuid = getGuidFromByteArray(token);

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(uuid.toString()))))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isOk())
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
                .andExpect(jsonPath("$.message").value(LoginResponseMessage.USER_NOT_EXIST.name()));
    }
}