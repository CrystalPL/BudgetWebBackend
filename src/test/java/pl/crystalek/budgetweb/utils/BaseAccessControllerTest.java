package pl.crystalek.budgetweb.utils;

import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class BaseAccessControllerTest {
    static String[][] EMPTY_TEST = new String[][]{{null, null}};
    protected MockMvc mockMvc;
    protected UserAccountUtil userAccountUtil;

    /**
     * Określa endpointy, do których dostęp powinien być możliwy bez logowania.
     *
     * @return tablica ścieżek i metod HTTP, które nie wymagają uwierzytelnienia
     */
    protected String[][] shouldAllowAccessWithoutAccount() {
        return EMPTY_TEST;
    }

    /**
     * Określa endpointy, do których dostęp powinien być zabroniony bez logowania.
     *
     * @return tablica ścieżek i metod HTTP, które wymagają uwierzytelnienia
     */
    protected String[][] shouldDeniedAccessWithoutAccount() {
        return EMPTY_TEST;
    }

    /**
     * Określa endpointy, do których dostęp powinien być możliwy dla użytkowników z rolą użytkownika.
     *
     * @return tablica ścieżek i metod HTTP, które są dostępne dla zwykłego użytkownika
     */
    protected String[][] shouldAllowAccessWithAccount() {
        return EMPTY_TEST;
    }

    /**
     * Określa endpointy, do których dostęp powinien być zabroniony nawet dla użytkowników z rolą użytkownika.
     *
     * @return tablica ścieżek i metod HTTP, do których zwykły użytkownik nie ma dostępu
     */
    protected String[][] shouldDeniedAccessWithAccount() {
        return EMPTY_TEST;
    }

    @ParameterizedTest
    @MethodSource("shouldAllowAccessWithoutAccount")
    void shouldAllowAccessWithoutAccount(final String path, final String httpMethod) throws Exception {
        Assumptions.assumeTrue(shouldAllowAccessWithoutAccount()[0][0] != null, "Brak danych testowych – test pominięty");

        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path))
                .andExpect(result -> assertNotEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus()));
    }

    @ParameterizedTest
    @MethodSource("shouldDeniedAccessWithoutAccount")
    void shouldDeniedAccessWithoutAccount(final String path, final String httpMethod) throws Exception {
        Assumptions.assumeTrue(shouldDeniedAccessWithoutAccount()[0][0] != null, "Brak danych testowych – test pominięty");

        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("shouldAllowAccessWithAccount")
    void shouldAllowAccessWithAccount(final String path, final String httpMethod) throws Exception {
        Assumptions.assumeTrue(shouldAllowAccessWithAccount()[0][0] != null, "Brak danych testowych – test pominięty");

        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();
        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path).cookie(cookie))
                .andExpect(result -> assertNotEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertNotEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus()));
    }

    @ParameterizedTest
    @MethodSource("shouldDeniedAccessWithAccount")
    void shouldDeniedAccessWithAccount(final String path, final String httpMethod) throws Exception {
        Assumptions.assumeTrue(shouldDeniedAccessWithAccount()[0][0] != null, "Brak danych testowych – test pominięty");

        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();
        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path).cookie(cookie))
                .andExpect(status().isForbidden());
    }
}