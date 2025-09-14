package pl.crystalek.budgetweb.auth.controller;

import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.helper.BaseAccessControllerTest;
import pl.crystalek.budgetweb.helper.request.RequestHelper;
import pl.crystalek.budgetweb.helper.request.ResponseData;
import pl.crystalek.budgetweb.token.TokenProperties;
import pl.crystalek.budgetweb.token.model.RefreshToken;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class LogoutControllerTest extends BaseAccessControllerTest {
    TokenProperties tokenProperties;

    @Override
    protected String[][] shouldDeniedAccessWithoutAccount() {
        return new String[][]{{"/auth/logout", "POST"}};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/auth/logout", "POST"}};
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        final ResponseData responseData = RequestHelper.builder()
                .path("/auth/logout")
                .httpMethod(HttpMethod.POST)
                .withUser(userAccountUtil)
                .expectedResponseCode(HttpStatus.OK)
                .build().sendRequest(mockMvc);

        final List<RefreshToken> tokenList = entityManager.createQuery("select rt from RefreshToken rt", RefreshToken.class).getResultList();
        assertEquals(0, tokenList.size());
        final Cookie[] cookies = responseData.getResultActions().andReturn()
                .getResponse()
                .getCookies();
        final Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                .filter(x -> x.getName().equalsIgnoreCase(tokenProperties.getCookieName()))
                .findFirst();
        assertEquals(0, cookieOptional.get().getMaxAge());
    }
}
