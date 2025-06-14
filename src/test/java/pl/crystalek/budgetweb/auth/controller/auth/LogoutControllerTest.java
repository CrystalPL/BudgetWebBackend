package pl.crystalek.budgetweb.auth.controller.auth;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.auth.token.TokenProperties;
import pl.crystalek.budgetweb.auth.token.model.RefreshToken;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.request.RequestHelper;
import pl.crystalek.budgetweb.utils.request.ResponseData;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogoutControllerTest extends BaseAccessControllerTest {
    @Autowired
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
