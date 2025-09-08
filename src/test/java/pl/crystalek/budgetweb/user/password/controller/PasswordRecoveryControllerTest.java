package pl.crystalek.budgetweb.user.password.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.share.validation.email.EmailValidationErrorType;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.password.request.PasswordRecoveryRequest;
import pl.crystalek.budgetweb.user.password.response.PasswordRecoveryResponseMessage;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordRecoveryControllerTest extends BaseAccessControllerTest {
    static Stream<Arguments> provideInvalidData() {
        return Stream.of(
                Arguments.of("", EmailValidationErrorType.MISSING_EMAIL),
                Arguments.of("test", EmailValidationErrorType.INVALID_EMAIL),
                Arguments.of("test@", EmailValidationErrorType.INVALID_EMAIL),
                Arguments.of("test@example", EmailValidationErrorType.INVALID_EMAIL),
                Arguments.of("test@example.", EmailValidationErrorType.INVALID_EMAIL),
                Arguments.of(".test@example.com", EmailValidationErrorType.INVALID_EMAIL),
                Arguments.of("test@example.com", PasswordRecoveryResponseMessage.USER_NOT_FOUND)
        );
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/auth/password/recovery", "POST"}};
    }

    @Override
    protected String[][] shouldAllowAccessWithoutAccount() {
        return new String[][]{{"/auth/password/recovery", "POST"}};
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void shouldFailValidationWhenInputIsInvalid(final String email, final Enum<?> errorMessage) throws Exception {
        final PasswordRecoveryRequest passwordRecoveryRequest = new PasswordRecoveryRequest(email);
        RequestHelper.builder()
                .path("/auth/password/recovery")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .content(passwordRecoveryRequest)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnUserNotFoundWhenUserNotExists() throws Exception {
        final PasswordRecoveryRequest passwordRecoveryRequest = new PasswordRecoveryRequest(UserAccountUtil.TESTING_USER.email());
        RequestHelper.builder()
                .path("/auth/password/recovery")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(PasswordRecoveryResponseMessage.USER_NOT_FOUND)
                .content(passwordRecoveryRequest)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldSendEmail() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);

        final PasswordRecoveryRequest passwordRecoveryRequest = new PasswordRecoveryRequest(UserAccountUtil.TESTING_USER.email());
        RequestHelper.builder()
                .path("/auth/password/recovery")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(PasswordRecoveryResponseMessage.SUCCESS)
                .content(passwordRecoveryRequest)
                .build().sendRequest(mockMvc);

        final List<ConfirmationToken> confirmationTokenList = entityManager.createQuery("select ct from ConfirmationToken ct", ConfirmationToken.class).getResultList();
        assertEquals(1, confirmationTokenList.size());
    }

    @Test
    void shouldResendEmail() throws Exception {
        userAccountUtil.register(UserAccountUtil.TESTING_USER);
        final User createdUser = userAccountUtil.getCreatedUser();
        entityManager.persist(new ConfirmationToken(createdUser, Instant.EPOCH, ConfirmationTokenType.PASSWORD_RECOVERY));
        entityManager.flush();

        final PasswordRecoveryRequest passwordRecoveryRequest = new PasswordRecoveryRequest(UserAccountUtil.TESTING_USER.email());
        RequestHelper.builder()
                .path("/auth/password/recovery")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(PasswordRecoveryResponseMessage.SUCCESS)
                .content(passwordRecoveryRequest)
                .build().sendRequest(mockMvc);

        final List<ConfirmationToken> confirmationTokenList = entityManager.createQuery("select ct from ConfirmationToken ct", ConfirmationToken.class).getResultList();
        assertEquals(1, confirmationTokenList.size());
        final ConfirmationToken confirmationToken = confirmationTokenList.get(0);
        assertEquals(Instant.EPOCH, confirmationToken.getExpireAt());
    }
}