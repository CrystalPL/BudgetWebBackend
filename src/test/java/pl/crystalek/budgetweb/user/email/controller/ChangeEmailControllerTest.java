package pl.crystalek.budgetweb.user.email.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;
import pl.crystalek.budgetweb.confirmation.ConfirmationTokenType;
import pl.crystalek.budgetweb.user.email.ChangeEmail;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.request.ChangeEmailRequest;
import pl.crystalek.budgetweb.user.response.ChangeEmailResponseMessage;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ChangeEmailControllerTest extends BaseAccessControllerTest {
    @NonFinal ChangeEmailRequest validChangeEmailRequest;

    private static Stream<Arguments> provideErrorCase() {
        return Stream.of(
                Arguments.of(
                        new ChangeEmailRequest("test@example.com", "test@example.com", UserAccountUtil.TESTING_USER.password()),
                        ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS
                ),
                Arguments.of(
                        new ChangeEmailRequest("test2@example.com", "test2@example.com", "123"),
                        ChangeEmailResponseMessage.BAD_CREDENTIALS
                )
        );
    }

    @Override
    protected String[][] shouldDeniedAccessWithoutAccount() {
        return new String[][]{{"/account/change-email", "POST"},};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/account/change-email", "POST"}};
    }

    @BeforeEach
    void setUp() {
        validChangeEmailRequest = new ChangeEmailRequest(
                "test2@example.com",
                "test2@example.com",
                UserAccountUtil.TESTING_USER.password()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "test@example.com, inny@example.com, haslo123, EMAIL_MISMATCH",
            "niepoprawny-email, niepoprawny-email, haslo123, INVALID_EMAIL",
            "test@example.com, test@example.com, '', MISSING_PASSWORD",
            "test@example.com, test@example.com, , MISSING_PASSWORD",
            "'', '', haslo123, MISSING_EMAIL",
            "test@example.com, , haslo123, MISSING_CONFIRM_EMAIL",
            ", '', haslo123, MISSING_EMAIL",
    })
    void shouldReturnErrorWhenChangeEmailFails(final String email, final String confirmEmail, final String password, final String expectedMessage) {
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(email, confirmEmail, password);
        sendChangeEmailRequest(changeEmailRequest, HttpStatus.BAD_REQUEST, expectedMessage);
    }

    @Test
    void shouldChangeEmailSuccessfully() {
        sendChangeEmailRequest(validChangeEmailRequest, HttpStatus.OK, ChangeEmailResponseMessage.SUCCESS.name());

        final User user = userAccountUtil.getCreatedUser();
        final List<ConfirmationToken> confirmationTokenList = entityManager.createQuery("select ct from ConfirmationToken ct", ConfirmationToken.class).getResultList();
        final ConfirmationToken confirmationToken = confirmationTokenList.getFirst();
        final Instant emailExpirationTime = Instant.now().plus(Duration.ofDays(1));
        final Duration between = Duration.between(emailExpirationTime, confirmationToken.getExpireAt()).abs();

        final List<ChangeEmail> changeEmailList = entityManager.createQuery("select ce from ChangeEmail ce", ChangeEmail.class).getResultList();
        final ChangeEmail first = changeEmailList.getFirst();

        assertEquals(user, confirmationToken.getUser());
        assertEquals(ConfirmationTokenType.CHANGE_EMAIL, confirmationToken.getConfirmationTokenType());
        assertTrue(between.toMillis() > 0);
        assertTrue(between.toMillis() < 2000);

        assertEquals("test2@example.com", first.getNewEmail());
        assertEquals(first.getId(), confirmationToken.getId());
    }

    @ParameterizedTest
    @MethodSource("provideErrorCase")
    void shouldReturnError(final ChangeEmailRequest changeEmailRequest, final ChangeEmailResponseMessage expectedResponseMessage) {
        sendChangeEmailRequest(changeEmailRequest, HttpStatus.BAD_REQUEST, expectedResponseMessage.name());

    }

    @Test
    @Transactional
    void shouldReturnEmailAlreadyExists() {
        final User user = new User("abc@abc.com", "123", false);
        entityManager.persist(user);
        final ConfirmationToken confirmationToken = new ConfirmationToken(user, Instant.now(), ConfirmationTokenType.CHANGE_EMAIL);
        entityManager.persist(confirmationToken);
        final ChangeEmail changeEmail = new ChangeEmail(confirmationToken, "test2@example.com");
        entityManager.persist(changeEmail);

        sendChangeEmailRequest(validChangeEmailRequest, HttpStatus.BAD_REQUEST, ChangeEmailResponseMessage.EMAIL_ALREADY_EXISTS.name());
    }

    @Test
    @Transactional
    void shouldReturnSuccessWhenSendRequestTwoTimes() {
        sendChangeEmailRequest(validChangeEmailRequest, HttpStatus.OK, ChangeEmailResponseMessage.SUCCESS.name());

        final List<ChangeEmail> changeEmailList = entityManager.createQuery("select ce from ChangeEmail ce", ChangeEmail.class).getResultList();
        final List<ConfirmationToken> confirmationTokenList = entityManager.createQuery("select ct From ConfirmationToken ct", ConfirmationToken.class).getResultList();
        final ChangeEmail changeEmailFirst = changeEmailList.getFirst();
        final ConfirmationToken confirmationTokenFirst = confirmationTokenList.getFirst();

        RequestHelper.builder()
                .loginUser(userAccountUtil)
                .path("/account/change-email")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(ChangeEmailResponseMessage.SUCCESS)
                .content(validChangeEmailRequest)
                .build().sendRequest(mockMvc);

        final List<ChangeEmail> changeEmailListSecondTime = entityManager.createQuery("select ce from ChangeEmail ce", ChangeEmail.class).getResultList();
        final List<ConfirmationToken> confirmationTokenListSecondTime = entityManager.createQuery("select ct From ConfirmationToken ct", ConfirmationToken.class).getResultList();
        final ChangeEmail changeEmailSecond = changeEmailListSecondTime.getFirst();
        final ConfirmationToken confirmationTokenSecond = confirmationTokenListSecondTime.getFirst();

        assertEquals(1, changeEmailList.size());
        assertEquals(1, changeEmailListSecondTime.size());
        assertEquals(1, confirmationTokenListSecondTime.size());
        assertEquals(1, confirmationTokenList.size());
        assertNotEquals(changeEmailSecond.getId(), changeEmailFirst.getId());
        assertNotEquals(confirmationTokenSecond.getId(), confirmationTokenFirst.getId());
        assertNotEquals(confirmationTokenSecond.getExpireAt(), confirmationTokenFirst.getExpireAt());
    }

    private void sendChangeEmailRequest(final ChangeEmailRequest request, final HttpStatus expectedStatus, final String expectedMessage) {
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/change-email")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(expectedStatus)
                .expectedResponseMessage(expectedMessage)
                .content(request)
                .build().sendRequest(mockMvc);
    }
}