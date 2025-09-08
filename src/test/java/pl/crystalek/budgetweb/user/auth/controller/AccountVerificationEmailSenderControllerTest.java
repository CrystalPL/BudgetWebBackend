package pl.crystalek.budgetweb.user.auth.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.user.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.user.auth.request.ResendEmailRequest;
import pl.crystalek.budgetweb.user.auth.response.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

class AccountVerificationEmailSenderControllerTest extends BaseAccessControllerTest {

    @Override
    protected String[][] shouldDeniedAccessWithAccount() {
        return new String[][]{{"/auth/resend-email", "POST"}};
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
    void shouldFailValidationWhenInputIsInvalid(final String token, final String errorMessage) throws Exception {
        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/resend-email")
                .content(new ResendEmailRequest(token))
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldResendEmailSuccessfully() throws Exception {
        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);

        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/resend-email")
                .content(new ResendEmailRequest(confirmationToken.toString()))
                .expectedResponseCode(HttpStatus.OK)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldReturnErrorWhenConfirmationTokenHasExpired() throws Exception {
        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        entityManager.createQuery("update TemporaryUser tu set tu.expireAt = :expireAt")
                .setParameter("expireAt", Instant.now().minus(Duration.ofDays(30)))
                .executeUpdate();
        entityManager.clear();

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/resend-email")
                .content(new ResendEmailRequest(confirmationToken.toString()))
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldReturnErrorWhenAccountIsAlreadyConfirmed() throws Exception {

        RequestHelper.builder()
                .path("/auth/register")
                .httpMethod(HttpMethod.POST)
                .content(UserAccountUtil.TESTING_USER)
                .build().sendRequest(mockMvc);
        final UUID confirmationToken = userAccountUtil.getConfirmationToken();

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/confirm")
                .content(new AccountConfirmationRequest(confirmationToken.toString()))
                .build()
                .sendRequest(mockMvc);

        RequestHelper.builder()
                .httpMethod(HttpMethod.POST)
                .path("/auth/resend-email")
                .content(new ResendEmailRequest(confirmationToken.toString()))
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED)
                .build()
                .sendRequest(mockMvc);
    }
}
