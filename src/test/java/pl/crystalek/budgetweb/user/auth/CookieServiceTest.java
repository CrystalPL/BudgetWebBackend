package pl.crystalek.budgetweb.user.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import pl.crystalek.budgetweb.token.TokenProperties;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
class CookieServiceTest {
    static final String COOKIE_NAME = "AUTH_TOKEN";
    static final String ACCESS_TOKEN = "abc123";

    @Mock TokenProperties tokenProperties;
    @Mock HttpServletResponse response;
    @InjectMocks CookieService cookieService;

    @BeforeEach
    void setUp() {
        when(tokenProperties.getCookieName()).thenReturn(COOKIE_NAME);
    }

    @Test
    void shouldCreateCookieAndAddToResponseWithRememberMeTrue() {
        cookieService.createCookieAndAddToResponse(ACCESS_TOKEN, true, response);

        final ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        final Cookie cookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, cookie.getName());
        assertEquals(ACCESS_TOKEN, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.getSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(CookieService.COOKIE_MAX_AGE, cookie.getMaxAge());
    }

    @Test
    void shouldCreateCookieAndAddToResponseWithRememberMeFalse() {
        cookieService.createCookieAndAddToResponse(ACCESS_TOKEN, false, response);

        final ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        final Cookie cookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, cookie.getName());
        assertEquals(ACCESS_TOKEN, cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.getSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(-1, cookie.getMaxAge());
    }

    @Test
    void shouldDeleteCookie() {
        cookieService.deleteCookie(response);

        final ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        final Cookie cookie = cookieCaptor.getValue();
        assertEquals(COOKIE_NAME, cookie.getName());
        assertNull(cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.getSecure());
        assertEquals("/", cookie.getPath());
    }

    @Test
    void shouldGetCookieWithTokenWhenCookiePresent() {
        final Cookie cookie1 = new Cookie("other", "value");
        final Cookie cookie2 = new Cookie(COOKIE_NAME, ACCESS_TOKEN);
        final Cookie[] cookies = new Cookie[]{cookie1, cookie2};

        final Optional<Cookie> result = cookieService.getCookieWithToken(cookies);
        assertTrue(result.isPresent());
        assertEquals(COOKIE_NAME, result.get().getName());
        assertEquals(ACCESS_TOKEN, result.get().getValue());
    }

    @Test
    void shouldGetCookieWithTokenWhenCookieMissing() {
        final Cookie cookie1 = new Cookie("other", "value");
        final Cookie[] cookies = new Cookie[]{cookie1};

        final Optional<Cookie> result = cookieService.getCookieWithToken(cookies);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldGetCookieWithTokenWhenArrayIsNull() {
        final Optional<Cookie> result = cookieService.getCookieWithToken(null);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldGetCookieWithTokenWhenBlankOrEmptyValue() {
        final Cookie emptyCookie = new Cookie(COOKIE_NAME, "");
        final Cookie blankCookie = new Cookie(COOKIE_NAME, " ");
        final Cookie[] cookies = new Cookie[]{emptyCookie, blankCookie};

        final Optional<Cookie> result = cookieService.getCookieWithToken(cookies);
        assertFalse(result.isPresent());
    }
}