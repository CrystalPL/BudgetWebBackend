package pl.crystalek.budgetweb.user.password.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.token.model.RefreshToken;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.password.request.PasswordRecoveryRequest;
import pl.crystalek.budgetweb.user.password.request.PasswordResetRequest;
import pl.crystalek.budgetweb.user.password.response.PasswordResetResponseMessage;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordResetControllerTest extends BaseAccessControllerTest {
    static Stream<Arguments> provideInvalidData() {
        return Stream.of(
                Arguments.of("", "", "", "MISSING_TOKEN"),
                Arguments.of(null, "", "", "MISSING_TOKEN"),
                Arguments.of("123", "StrongPassword1!", "StrongPassword1!", "INVALID_TOKEN"),
                Arguments.of("dddd-dddd-dddd-dddd", "StrongPassword1!", "StrongPassword1!", "INVALID_TOKEN"),
                Arguments.of(UUID.randomUUID().toString(), "", "", "MISSING_PASSWORD"),
                Arguments.of(UUID.randomUUID().toString(), null, "", "MISSING_PASSWORD"),
                Arguments.of(UUID.randomUUID().toString(), "", null, "MISSING_PASSWORD"),
                Arguments.of(UUID.randomUUID().toString(), "StrongPassword1!", "StrongPassword1!2", "PASSWORD_MISMATCH")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void shouldFailValidationWhenInputIsInvalid(final String token, final String password, final String confirmPassword, final String expectedMessage) throws Exception {
        final PasswordResetRequest passwordResetRequest = new PasswordResetRequest(token, password, confirmPassword);
        RequestHelper.builder()
                .path("/auth/password/reset")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(expectedMessage)
                .content(passwordResetRequest)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnTokenExpiredWhenTokenNotExists() {
        final PasswordResetRequest passwordResetRequest = new PasswordResetRequest(UUID.randomUUID().toString(), "StrongPassword1!123", "StrongPassword1!123");
        RequestHelper.builder()
                .path("/auth/password/reset")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(PasswordResetResponseMessage.TOKEN_EXPIRED)
                .content(passwordResetRequest)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldResetPassword() {
        final PasswordRecoveryRequest passwordRecoveryRequest = new PasswordRecoveryRequest(UserAccountUtil.TESTING_USER.email());
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/auth/password/recovery")
                .httpMethod(HttpMethod.POST)
                .content(passwordRecoveryRequest)
                .build().sendRequest(mockMvc);

        final ConfirmationToken confirmationToken = entityManager.createQuery("select a from ConfirmationToken a", ConfirmationToken.class).getSingleResult();

        final PasswordResetRequest passwordResetRequest = new PasswordResetRequest(confirmationToken.getId().toString(), "StrongPassword1!123", "StrongPassword1!123");
        RequestHelper.builder()
                .loginUser(userAccountUtil)
                .path("/auth/password/reset")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(PasswordResetResponseMessage.SUCCESS)
                .content(passwordResetRequest)
                .build().sendRequest(mockMvc);

        final List<ConfirmationToken> confirmationTokenList = entityManager.createQuery("select ct from ConfirmationToken ct", ConfirmationToken.class).getResultList();
        final List<RefreshToken> resultTokenList = entityManager.createQuery("select rt from RefreshToken rt", RefreshToken.class).getResultList();

        final User user = userAccountUtil.getCreatedUser();
        assertEquals(0, confirmationTokenList.size());
        assertEquals(0, resultTokenList.size());
        assertTrue(new BCryptPasswordEncoder().matches("StrongPassword1!123", user.getPassword()));
    }
}

