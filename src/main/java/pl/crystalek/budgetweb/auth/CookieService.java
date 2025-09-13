package pl.crystalek.budgetweb.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.token.TokenProperties;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CookieService {
    public static final int COOKIE_MAX_AGE = 10 * 365 * 24 * 60 * 60; // 10 lat w sekundach
    TokenProperties tokenProperties;

    public void deleteCookie(final HttpServletResponse response) {
        final Cookie cookie = new Cookie(tokenProperties.getCookieName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    public void createCookieAndAddToResponse(final String accessToken, final boolean rememberMe, final HttpServletResponse response) {
        final Cookie cookie = new Cookie(tokenProperties.getCookieName(), accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        if (rememberMe) {
            cookie.setMaxAge(COOKIE_MAX_AGE);
        }

        response.addCookie(cookie);
    }

    public Optional<Cookie> getCookieWithToken(final Cookie[] cookies) {
        return Optional.ofNullable(cookies).flatMap(this::getCookie);
    }

    private Optional<Cookie> getCookie(final Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenProperties.getCookieName()))
                .filter(cookie -> StringUtils.isNotEmpty(cookie.getValue()) && StringUtils.isNotBlank(cookie.getValue()))
                .findFirst();
    }
}
