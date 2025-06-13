package pl.crystalek.budgetweb.auth.controller.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.Cleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pl.crystalek.budgetweb.auth.controller.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.AccountConfirmationResponseMessage;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserData;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfirmAccountControllerTest extends BaseAccessControllerTest {
    private Validator validator;

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
        RequestHelper.builder()
                .path("/auth/confirm")
                .httpMethod("POST")
                .content(new AccountConfirmationRequest(token))
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .build()
                .sendRequest(mockMvc);
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

        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .content(request)
                .build().sendRequest(mockMvc);

        final UUID uuid = userAccountUtil.getConfirmationToken();

        RequestHelper.builder()
                .path("/auth/confirm")
                .httpMethod(HttpMethod.POST)
                .content(new AccountConfirmationRequest(uuid.toString()))
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(AccountConfirmationResponseMessage.SUCCESS)
                .build()
                .sendRequest(mockMvc);

        final List<TemporaryUser> list = entityManager.createQuery("select tu from TemporaryUser tu", TemporaryUser.class).getResultList();
        final List<User> userList = entityManager.createQuery("select u from User u", User.class).getResultList();
        final List<UserData> userDataList = entityManager.createQuery("select ud from UserData  ud", UserData.class).getResultList();

        assertEquals(0, list.size());
        assertEquals(1, userList.size());
        assertEquals(1, userDataList.size());

        assertEquals(email, userList.getFirst().getEmail());

        assertTrue(BCrypt.checkpw(password, userList.getFirst().getPassword()));
        assertEquals(receiveUpdates, userList.getFirst().isReceiveUpdates());
        assertNull(userList.getFirst().getHouseholdMember());

        assertEquals(userDataList.getFirst().getId(), userList.getFirst().getId());
        assertEquals(username, userDataList.getFirst().getNickname());
        assertEquals(userList.getFirst(), userDataList.getFirst().getUser());
        assertNull(userDataList.getFirst().getReceipts());
        assertNull(userDataList.getFirst().getItemsToReturnMoney());

        userAccountUtil.login(new LoginRequest(email, password, false));
    }

    @Test
    void shouldReturnTokenExpiredWhenTokenIsNotExists() throws Exception {
        RequestHelper.builder()
                .path("/auth/confirm")
                .httpMethod(HttpMethod.POST)
                .content(new AccountConfirmationRequest(UUID.randomUUID().toString()))
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(AccountConfirmationResponseMessage.TOKEN_EXPIRED)
                .build()
                .sendRequest(mockMvc);
    }
}
