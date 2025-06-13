package pl.crystalek.budgetweb.auth.controller.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.response.RegisterResponseMessage;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterControllerTest extends BaseAccessControllerTest {

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

        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .content(request)
                .build().sendRequest(mockMvc);
    }
}
