package pl.crystalek.budgetweb.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import pl.crystalek.budgetweb.auth.controller.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.ResendEmailRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ResendEmailControllerTest extends BaseAccessControllerTest {
    JdbcTemplate jdbcTemplate;
    EntityManager entityManager;
    ObjectMapper objectMapper;
    @NonFinal Validator validator;

    @Autowired
    public ResendEmailControllerTest(final MockMvc mockMvc, final UserAccountUtil userAccountUtil, final JdbcTemplate jdbcTemplate, final EntityManager entityManager, final ObjectMapper objectMapper) {
        super(mockMvc, userAccountUtil);
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @Override
    protected String[][] shouldDeniedAccessWithAccount() {
        return new String[][]{{"/auth/resend-email", "POST"}};
    }

    @BeforeEach
    void setUp() {
        @Cleanup final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "'', MISSING_TOKEN",
            "'   ', MISSING_TOKEN",
            ", MISSING_TOKEN",
            "invalid-token, INVALID_TOKEN",
            "12345678-1234-1234-1234-123456789, INVALID_TOKEN",
            "12345678-1234-1234-1234-1234567890123, INVALID_TOKEN"
    })
    void shouldFailValidation(final String token, final String errorMessage) {
        // Given
        final ResendEmailRequest loginRequest = new ResendEmailRequest(token);

        // When
        final Set<ConstraintViolation<ResendEmailRequest>> violations = validator.validate(loginRequest, ResendEmailRequest.ResendEmailValidation.class);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(errorMessage)));
    }

    @ParameterizedTest
    @CsvSource({
            "'', MISSING_TOKEN",
            "'   ', MISSING_TOKEN",
            ", MISSING_TOKEN",
            "invalid-token, INVALID_TOKEN",
            "12345678-1234-1234-1234-123456789, INVALID_TOKEN",
            "12345678-1234-1234-1234-1234567890123, INVALID_TOKEN"
    })
    void shouldFailRequest(final String token, final String errorMessage) throws Exception {
        mockMvc.perform(post("/auth/resend-email")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ResendEmailRequest(token))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
    }

    @Test
    void shouldResendEmailSuccessfully() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        mockMvc.perform(post("/auth/resend-email")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ResendEmailRequest(confirmationToken.toString()))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnErrorWhenConfirmationTokenHasExpired() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        jdbcTemplate.update("UPDATE temporary_users SET expire_at = ?", Instant.now().minus(Duration.ofDays(30)));
        entityManager.clear();

        mockMvc.perform(post("/auth/resend-email")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ResendEmailRequest(confirmationToken.toString()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED.name()))
                .andDo(print());
    }

    @Test
    void shouldReturnErrorWhenAccountIsAlreadyConfirmed() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(confirmationToken.toString()))))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/auth/resend-email")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ResendEmailRequest(confirmationToken.toString()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED.name()))
                .andDo(print());
    }
}
