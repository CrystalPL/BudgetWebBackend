package pl.crystalek.budgetweb.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.crystalek.budgetweb.helper.BaseAccessControllerTest;
import pl.crystalek.budgetweb.helper.UserAccountUtil;
import pl.crystalek.budgetweb.helper.request.RequestHelper;
import pl.crystalek.budgetweb.share.validation.password.PasswordValidationErrorType;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.request.ChangePasswordRequest;
import pl.crystalek.budgetweb.user.response.ChangePasswordResponseMessage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChangePasswordControllerTest extends BaseAccessControllerTest {

    static Stream<Arguments> provideInvalidChangePasswordData() {
        final String validOld = "OldPass1!";
        final String strong = "StrongPass1!";
        final String tooShort = "Ab1!";
        final String noUpper = "strongpass1!";
        final String noLower = "STRONGPASS1!";
        final String noNumber = "StrongPass!";
        final String noSpecial = "StrongPass1";
        final String tooLong = "A" + "a1!".repeat(634) + "Z";

        return Stream.of(
                // old password missing
                Arguments.of("", strong, strong, "MISSING_OLD_PASSWORD"),
                Arguments.of(null, null, strong, "MISSING_OLD_PASSWORD"),

                // password missing
                Arguments.of(validOld, "", "", PasswordValidationErrorType.MISSING_PASSWORD.name()),
                Arguments.of(validOld, null, null, PasswordValidationErrorType.MISSING_PASSWORD.name()),

                // password specific rule violations
                Arguments.of(validOld, tooShort, tooShort, PasswordValidationErrorType.PASSWORD_TOO_SHORT.name()),
                Arguments.of(validOld, noUpper, noUpper, PasswordValidationErrorType.MISSING_UPPERCASE.name()),
                Arguments.of(validOld, noLower, noLower, PasswordValidationErrorType.MISSING_LOWERCASE.name()),
                Arguments.of(validOld, noNumber, noNumber, PasswordValidationErrorType.MISSING_NUMBER.name()),
                Arguments.of(validOld, noSpecial, noSpecial, PasswordValidationErrorType.MISSING_SPECIAL_CHAR.name()),
                Arguments.of(validOld, tooLong, tooLong, PasswordValidationErrorType.PASSWORD_TOO_LONG.name()),

                // confirm password missing
                Arguments.of(validOld, strong, "", "MISSING_CONFIRM_PASSWORD"),
                Arguments.of(validOld, strong, null, "MISSING_CONFIRM_PASSWORD"),

                // mismatch
                Arguments.of(validOld, strong, strong + "X", "PASSWORD_MISMATCH")
        );
    }

    @Override
    protected String[][] shouldDeniedAccessWithoutAccount() {
        return new String[][]{{"/account/change-password", "POST"},};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/account/change-password", "POST"}};
    }

    @ParameterizedTest
    @MethodSource("provideInvalidChangePasswordData")
    void shouldFailValidationWhenInputInvalid(final String oldPassword,
                                              final String password,
                                              final String confirmPassword,
                                              final String expectedMessage
    ) {
        final ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, password, confirmPassword);

        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/change-password")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(expectedMessage)
                .content(request)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnBadCredentialsWhenPasswordIsIncorrect() {
        userAccountUtil.registerDefaultUser();
        final String newPassword = "ValidNewPass1!";
        final ChangePasswordRequest request = new ChangePasswordRequest("WrongOldPass1!", newPassword, newPassword);

        RequestHelper.builder()
                .loginUser(userAccountUtil)
                .path("/account/change-password")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(ChangePasswordResponseMessage.BAD_CREDENTIALS)
                .content(request)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldChangePassword() {
        final String oldPassword = UserAccountUtil.TESTING_USER.password();
        final String newPassword = "NewStrongPass1!";
        final ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, newPassword);

        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/change-password")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(ChangePasswordResponseMessage.SUCCESS)
                .content(request)
                .build().sendRequest(mockMvc);

        entityManager.clear();
        final User user = userAccountUtil.getCreatedUser();
        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertTrue(encoder.matches(newPassword, user.getPassword()));
        assertFalse(encoder.matches(oldPassword, user.getPassword()));
    }
}
