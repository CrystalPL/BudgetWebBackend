package pl.crystalek.budgetweb.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.helper.BaseAccessControllerTest;
import pl.crystalek.budgetweb.helper.request.RequestHelper;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.request.ChangeNicknameRequest;
import pl.crystalek.budgetweb.user.response.ChangeNicknameResponseMessage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeNicknameControllerTest extends BaseAccessControllerTest {
    static Stream<Arguments> provideInvalidNicknameData() {
        return Stream.of(
                Arguments.of("", "MISSING_USERNAME"),
                Arguments.of(null, "MISSING_USERNAME"),
                Arguments.of("a".repeat(533), "TOO_LONG_USERNAME")
        );
    }

    @Override
    protected String[][] shouldDeniedAccessWithoutAccount() {
        return new String[][]{{"/account/change-nickname", "POST"}};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/account/change-nickname", "POST"}};
    }

    @ParameterizedTest
    @MethodSource("provideInvalidNicknameData")
    void shouldFailValidationWhenNicknameInvalid(final String nickname, final String expectedMessage) {
        final ChangeNicknameRequest request = new ChangeNicknameRequest(nickname);

        RequestHelper.builder()
                .withUser(userAccountUtil)
                .path("/account/change-nickname")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(expectedMessage)
                .content(request)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldChangeNickname() {
        userAccountUtil.registerDefaultUser();
        final String newNickname = "NowyNick123";
        final ChangeNicknameRequest request = new ChangeNicknameRequest(newNickname);

        RequestHelper.builder()
                .loginUser(userAccountUtil)
                .path("/account/change-nickname")
                .httpMethod(HttpMethod.POST)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(ChangeNicknameResponseMessage.SUCCESS)
                .content(request)
                .build()
                .sendRequest(mockMvc);

        entityManager.clear();
        final User updatedUser = userAccountUtil.getCreatedUser();
        final String newName = updatedUser.getUserData().getNickname();

        assertEquals(newNickname, newName);
    }
}
