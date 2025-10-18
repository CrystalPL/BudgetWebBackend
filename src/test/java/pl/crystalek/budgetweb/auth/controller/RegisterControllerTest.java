package pl.crystalek.budgetweb.auth.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pl.crystalek.budgetweb.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.response.RegisterResponseMessage;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.helper.BaseAccessControllerTest;
import pl.crystalek.budgetweb.helper.UserAccountUtil;
import pl.crystalek.budgetweb.helper.request.RequestHelper;
import pl.crystalek.budgetweb.user.profile.email.ChangeEmail;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;
import pl.crystalek.budgetweb.user.validator.password.PasswordValidationErrorType;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterControllerTest extends BaseAccessControllerTest {

    private static Stream<Arguments> provideInvalidRegisterRequests() {
        return Stream.of(
                Arguments.of(new RegisterRequest(" ", null, null, null, null, null), "MISSING_USERNAME"),
                Arguments.of(new RegisterRequest(null, null, null, null, null, null), "MISSING_USERNAME"),
                Arguments.of(new RegisterRequest("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", null, null, null, null, null), "TOO_LONG_USERNAME"),
                Arguments.of(new RegisterRequest("test", "", null, null, null, null), "MISSING_EMAIL"),
                Arguments.of(new RegisterRequest("test", null, null, null, null, null), "MISSING_EMAIL"),
                Arguments.of(new RegisterRequest("test", "invalid.email.com", null, null, null, null), "INVALID_EMAIL"),
                Arguments.of(new RegisterRequest("test", "invalid.email@", null, null, null, null), "INVALID_EMAIL"),
                Arguments.of(new RegisterRequest("test", "@example.com", null, null, null, null), "INVALID_EMAIL"),
                Arguments.of(new RegisterRequest("test", "valid.email@domain", null, null, null, null), "INVALID_EMAIL"),
                Arguments.of(new RegisterRequest("test", "invalid!email@example.com", null, null, null, null), "INVALID_EMAIL"),
                Arguments.of(new RegisterRequest("test", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@example.pl", null, null, null, null), "EMAIL_TOO_LONG"),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", null, null, null, null), "MISSING_CONFIRM_EMAIL"),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "", null, null, null), "MISSING_CONFIRM_EMAIL"),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.emaail@example.com", null, null, null), "EMAIL_MISMATCH"),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", null, null, null), PasswordValidationErrorType.MISSING_PASSWORD.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "", null, null), PasswordValidationErrorType.MISSING_PASSWORD.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "aaaaaaa", null, null), PasswordValidationErrorType.PASSWORD_TOO_SHORT.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", null, null), PasswordValidationErrorType.PASSWORD_TOO_LONG.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "strongpassword1!", null, null), PasswordValidationErrorType.MISSING_UPPERCASE.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "STRONGPASSWORD1!", null, null), PasswordValidationErrorType.MISSING_LOWERCASE.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "StrongPassword!", null, null), PasswordValidationErrorType.MISSING_NUMBER.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "StrongPassword1", null, null), PasswordValidationErrorType.MISSING_SPECIAL_CHAR.name()),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "StrongPassword1!", null, null), "MISSING_CONFIRM_PASSWORD"),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "StrongPassword1!", "", null), "MISSING_CONFIRM_PASSWORD"),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "StrongPassword1!", "DtrongPassword1!", null), "PASSWORD_MISMATCH"),
                Arguments.of(new RegisterRequest("test", "valid.email@example.com", "valid.email@example.com", "StrongPassword1!", "StrongPassword1!", null), "MISSING_RECEIVE_UPDATES")
        );
    }

    @Override
    protected String[][] shouldAllowAccessWithoutAccount() {
        return new String[][]{{"/auth/register", "POST"}};
    }

    @Override
    protected String[][] shouldDeniedAccessWithAccount() {
        return new String[][]{{"/auth/register", "POST"}};
    }

    @Test
    void shouldRegisterSuccessfullyWhenCredentialsAreValid() {
        final RegisterRequest request = new RegisterRequest(
                "ValidUsername",
                "valid.email@example.com",
                "valid.email@example.com",
                "StrongPassword1!",
                "StrongPassword1!",
                true
        );

        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .content(request)
                .build().sendRequest(mockMvc);

        final List<TemporaryUser> users = entityManager.createQuery("select tu from TemporaryUser tu", TemporaryUser.class).getResultList();

        assertEquals(1, users.size());
        assertEquals(request.username(), users.getFirst().getNickname());
        assertEquals(request.email(), users.getFirst().getEmail());
        assertTrue(BCrypt.checkpw(request.password(), users.getFirst().getPassword()));
        assertTrue(BCrypt.checkpw(request.confirmPassword(), users.getFirst().getPassword()));
        assertEquals(request.receiveUpdates(), users.getFirst().isReceiveUpdates());
    }

    @Test
    void shouldReturnAccountNotEnabledWhenAccountIsNotConfirmed() {
        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);

        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(RegisterResponseMessage.ACCOUNT_NOT_ENABLED)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnAccountExistsWhenEmailIsAlreadyUsed() {
        userAccountUtil.registerDefaultUser();

        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(RegisterResponseMessage.ACCOUNT_EXISTS)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnAccountExistsWhenEmailUsedForChangingEmail() {
        entityManager.persist(new ChangeEmail(new ConfirmationToken(), UserAccountUtil.TESTING_USER.email()));
        entityManager.flush();
        entityManager.clear();
        userAccountUtil.registerDefaultUser();

        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(RegisterResponseMessage.ACCOUNT_EXISTS)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRegisterRequests")
    void shouldFailValidationWhenInputIsInvalid(final RegisterRequest request, final String errorMessage) {
        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .content(request)
                .build().sendRequest(mockMvc);
    }
}
