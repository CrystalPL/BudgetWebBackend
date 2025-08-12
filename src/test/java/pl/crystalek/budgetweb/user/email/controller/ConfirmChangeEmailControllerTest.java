package pl.crystalek.budgetweb.user.email.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.user.email.ChangeEmail;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.request.ConfirmEmailChangingRequest;
import pl.crystalek.budgetweb.user.response.ConfirmEmailChangingResponseMessage;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfirmChangeEmailControllerTest extends BaseAccessControllerTest {

    @Override
    protected String[][] shouldAllowAccessWithoutAccount() {
        return new String[][]{{"/account/confirm-change-email", "GET"}};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/account/confirm-change-email", "POST"}};
    }

    @BeforeEach
    void setUp() {

    }

    @ParameterizedTest
    @CsvSource({
            "'', MISSING_TOKEN",
            "' ', MISSING_TOKEN",
            ", MISSING_TOKEN",
            "123456, INVALID_TOKEN",
            "123e4567-e89b-12d3-a456-42661417400, INVALID_TOKEN",
            "123e4567-e89b-12d3-a456-4266141740000, INVALID_TOKEN",
            "123e4567-e89b-12d3-a456+426614174000, INVALID_TOKEN",
            "123e4567_e89b_12d3_a456_426614174000, INVALID_TOKEN",

    })
    void shouldFailValidation(final String token, final String expectedMessage) {
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/confirm-change-email")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(expectedMessage)
                .content(new ConfirmEmailChangingRequest(token))
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnTokenExpired() {
        final ConfirmEmailChangingRequest confirmEmailChangingRequest = new ConfirmEmailChangingRequest(UUID.randomUUID().toString());
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/confirm-change-email")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(ConfirmEmailChangingResponseMessage.TOKEN_EXPIRED)
                .content(confirmEmailChangingRequest)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnTokenNotFound() {
        final User user = new User("abc@abc.com", "123", false);
        entityManager.persist(user);
        final ConfirmationToken confirmationToken = new ConfirmationToken(user, Instant.now(), ConfirmationTokenType.CHANGE_EMAIL);
        entityManager.persist(confirmationToken);

        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/confirm-change-email")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(ConfirmEmailChangingResponseMessage.TOKEN_NOT_FOUND)
                .content(new ConfirmEmailChangingRequest(confirmationToken.getId().toString()))
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldChangeEmailSuccessfully() {
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest("test2@example.com", "test2@example.com", UserAccountUtil.TESTING_USER.password());
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/change-email")
                .httpMethod(HttpMethod.POST)
                .content(changeEmailRequest)
                .build().sendRequest(mockMvc);

        final String token = entityManager.createQuery("select ce from ChangeEmail ce", ChangeEmail.class).getSingleResult().getId().toString();
        RequestHelper.builder()
                .path("/account/confirm-change-email")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(ConfirmEmailChangingResponseMessage.SUCCESS)
                .content(new ConfirmEmailChangingRequest(token))
                .build().sendRequest(mockMvc);

        final User createdUser = userAccountUtil.getCreatedUser();
        final List<ChangeEmail> changeEmailList = entityManager.createQuery("select ce from ChangeEmail ce", ChangeEmail.class).getResultList();
        final List<ConfirmationToken> confirmationTokenList = entityManager.createQuery("select ct From ConfirmationToken ct", ConfirmationToken.class).getResultList();

        assertEquals("test2@example.com", createdUser.getEmail());
        assertEquals(0, changeEmailList.size());
        assertEquals(0, confirmationTokenList.size());
    }
}
