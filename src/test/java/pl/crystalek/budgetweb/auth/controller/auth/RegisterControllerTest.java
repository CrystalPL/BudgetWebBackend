package pl.crystalek.budgetweb.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterResponseMessage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RegisterControllerTest {
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    JdbcTemplate jdbcTemplate;

    @Test
    void shouldRegisterSuccessfullyWhenCredentialsAreValid() throws Exception {
        final RegisterRequest request = new RegisterRequest(
                "ValidUsername",
                "valid.email@example.com",
                "valid.email@example.com",
                "StrongPassword1!",
                "StrongPassword1!",
                true
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAccountNotEnabledWhenAccountIsNotConfirmed() throws Exception {
        final String hashedPassword = new BCryptPasswordEncoder().encode("StrongPassword1!");
        jdbcTemplate.execute("INSERT INTO users (email, nickname, password, receive_updates, user_role) " +
                             "VALUES ('test@example.com', 'TestUser', '" + hashedPassword + "', 1, 'GUEST');");

        final RegisterRequest request = new RegisterRequest(
                "AnotherUser",
                "test@example.com",
                "test@example.com",
                "StrongPassword1!",
                "StrongPassword1!",
                true
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(RegisterResponseMessage.ACCOUNT_NOT_ENABLED.name()));
    }

    @Test
    void shouldReturnAccountExistsWhenEmailIsAlreadyUsed() throws Exception {
        final String hashedPassword = new BCryptPasswordEncoder().encode("StrongPassword1!");
        jdbcTemplate.execute("INSERT INTO users (email, nickname, password, receive_updates, user_role) " +
                             "VALUES ('test@example.com', 'TestUser', '" + hashedPassword + "', 1, 'USER');");

        final RegisterRequest request = new RegisterRequest(
                "AnotherUser",
                "test@example.com",
                "test@example.com",
                "StrongPassword1!",
                "StrongPassword1!",
                true
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(RegisterResponseMessage.ACCOUNT_EXISTS.name()));
    }

    @ParameterizedTest
    @CsvSource({
            "' ', , , , , , 'MISSING_USERNAME'",
            ", , , , , , 'MISSING_USERNAME'",
            "'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', , , , , , 'TOO_LONG_USERNAME'",
            "'test', '', , , , , 'MISSING_EMAIL'",
            "'test', , , , , , 'MISSING_EMAIL'",
            "'test', 'invalid.email.com', , , , , 'INVALID_EMAIL'",
            "'test', 'invalid.email@', , , , , 'INVALID_EMAIL'",
            "'test', '@example.com', , , , , 'INVALID_EMAIL'",
            "'test', 'valid.email@domain', , , , , 'INVALID_EMAIL'",
            "'test', 'invalid!email@example.com', , , , , 'INVALID_EMAIL'",
            "'test', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@example.pl', , , , , 'EMAIL_TOO_LONG'",
            "'test', 'valid.email@example.com', , , , , 'MISSING_CONFIRM_EMAIL'",
            "'test', 'valid.email@example.com', '', , , , 'MISSING_CONFIRM_EMAIL'",
            "'test', 'valid.email@example.com', 'valid.emaail@example.com', , , , 'EMAIL_MISMATCH'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', , , , 'MISSING_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', '', , , 'MISSING_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'aaaaaaa', , , 'PASSWORD_TOO_SHORT'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', , , 'PASSWORD_TOO_LONG'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'strongpassword1!', , , 'MISSING_UPPERCASE'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'STRONGPASSWORD1!', , , 'MISSING_LOWERCASE'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword!', , , 'MISSING_NUMBER'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1', , , 'MISSING_SPECIAL_CHAR'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', , , 'MISSING_CONFIRM_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', '', , 'MISSING_CONFIRM_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', 'DtrongPassword1!', , 'PASSWORD_MISMATCH'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', 'StrongPassword1!', , 'MISSING_RECEIVE_UPDATES'",
    })
    void shouldFailValidationWhenInputIsInvalid(final String username, final String email, final String confirmEmail,
                                                final String password, final String confirmPassword, final Boolean receiveUpdates,
                                                final String errorMessage) throws Exception {
        final RegisterRequest request = new RegisterRequest(username, email, confirmEmail, password, confirmPassword, receiveUpdates);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}
