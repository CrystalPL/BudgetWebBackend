package pl.crystalek.budgetweb.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import pl.crystalek.budgetweb.auth.controller.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.auth.token.TokenCreator;
import pl.crystalek.budgetweb.auth.token.TokenDecoder;
import pl.crystalek.budgetweb.auth.token.TokenProperties;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ConfirmAccountControllerTest extends BaseAccessControllerTest {
    ObjectMapper objectMapper;
    TokenProperties tokenProperties;
    TokenDecoder tokenDecoder;
    TokenCreator tokenCreator;
    @NonFinal Validator validator;

    @Autowired
    public ConfirmAccountControllerTest(final MockMvc mockMvc, final UserAccountUtil userAccountUtil, final ObjectMapper objectMapper, final TokenProperties tokenProperties, final TokenDecoder tokenDecoder, final TokenCreator tokenCreator) {
        super(mockMvc, userAccountUtil);
        this.objectMapper = objectMapper;
        this.tokenProperties = tokenProperties;
        this.tokenDecoder = tokenDecoder;
        this.tokenCreator = tokenCreator;
    }

    @Override
    protected String[][] shouldAllowAccessWithoutAccount() {
        return new String[][]{{"/auth/confirm", "POST"}};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/auth/confirm", "POST"}};
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
        final AccountConfirmationRequest loginRequest = new AccountConfirmationRequest(token);

        // When
        final Set<ConstraintViolation<AccountConfirmationRequest>> violations = validator.validate(loginRequest, AccountConfirmationRequest.AccountConfirmationValidation.class);

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
        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(token))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andDo(print());
    }

    @ParameterizedTest
    @CsvSource({
            "'janedoe', 'janedoe@example.com', 'janedoe@example.com', 'StrongPassword1!', 'StrongPassword1!', true",
            "'john_smith', 'john.smith@example.org', 'john.smith@example.org', 'Password123$', 'Password123$', false",
            "'emily.jones', 'emily_jones@example.net', 'emily_jones@example.net', 'ValidPass2@', 'ValidPass2@', true",
            "'samuel_adams', 'samuel.adams@example.com', 'samuel.adams@example.com', 'CorrectHorse3!', 'CorrectHorse3!', false",
            "'lucas_brown', 'lucas.brown@example.co', 'lucas.brown@example.co', 'Password!4', 'Password!4', true"
    })
    void shouldReturnSuccessResponseWhenAccountConfirmationIsSuccessful(final String username, final String email, final String confirmEmail,
                                                                        final String password, final String confirmPassword, final Boolean receiveUpdates) throws Exception {

        final RegisterRequest request = new RegisterRequest(username, email, confirmEmail, password, confirmPassword, receiveUpdates);
        userAccountUtil.register(request);

        final UUID uuid = userAccountUtil.getConfirmationToken();

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(uuid.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(AccountConfirmationResponseMessage.SUCCESS));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, password, false)))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnTokenExpiredWhenTokenIsNotExists() throws Exception {
        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(UUID.randomUUID().toString()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(AccountConfirmationResponseMessage.TOKEN_EXPIRED.name()));
    }
}
