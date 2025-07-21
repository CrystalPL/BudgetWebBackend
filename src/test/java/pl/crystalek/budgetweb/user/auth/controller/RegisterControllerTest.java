package pl.crystalek.budgetweb.user.auth.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pl.crystalek.budgetweb.share.validation.password.PasswordValidationErrorType;
import pl.crystalek.budgetweb.user.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.user.auth.response.RegisterResponseMessage;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

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
    void shouldRegisterSuccessfullyWhenCredentialsAreValid() throws Exception {
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
    void shouldReturnAccountNotEnabledWhenAccountIsNotConfirmed() throws Exception {
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
    void shouldReturnAccountExistsWhenEmailIsAlreadyUsed() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);

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
    void shouldFailValidationWhenInputIsInvalid(final RegisterRequest request, final String errorMessage) throws Exception {
        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .content(request)
                .build().sendRequest(mockMvc);
    }
}
