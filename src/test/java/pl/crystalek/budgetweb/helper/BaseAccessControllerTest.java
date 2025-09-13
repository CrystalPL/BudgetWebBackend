package pl.crystalek.budgetweb.helper;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.helper.request.RequestHelper;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseAccessControllerTest extends BaseTest {
    static String[][] EMPTY_TEST = new String[][]{{null, null}};

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

        RequestHelper.builder()
                .path(path)
                .httpMethod(httpMethod)
                .expect(result -> assertNotEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus()))
                .build().sendRequest(mockMvc);
    }

    @ParameterizedTest
    @MethodSource("shouldDeniedAccessWithoutAccount")
    void shouldDeniedAccessWithoutAccount(final String path, final String httpMethod) throws Exception {
        Assumptions.assumeTrue(shouldDeniedAccessWithoutAccount()[0][0] != null, "Brak danych testowych – test pominięty");

        RequestHelper.builder()
                .path(path)
                .httpMethod(httpMethod)
                .expectedResponseCode(HttpStatus.UNAUTHORIZED)
                .build().sendRequest(mockMvc);
    }

    @ParameterizedTest
    @MethodSource("shouldAllowAccessWithAccount")
    void shouldAllowAccessWithAccount(final String path, final String httpMethod) throws Exception {
        Assumptions.assumeTrue(shouldAllowAccessWithAccount()[0][0] != null, "Brak danych testowych – test pominięty");

        RequestHelper.builder()
                .path(path)
                .httpMethod(httpMethod)
                .withUser(userAccountUtil)
                .expect(result -> assertNotEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()))
                .expect(result -> assertNotEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus()))
                .build().sendRequest(mockMvc);
    }

    @ParameterizedTest
    @MethodSource("shouldDeniedAccessWithAccount")
    void shouldDeniedAccessWithAccount(final String path, final String httpMethod) throws Exception {
        Assumptions.assumeTrue(shouldDeniedAccessWithAccount()[0][0] != null, "Brak danych testowych – test pominięty");

        RequestHelper.builder()
                .path(path)
                .httpMethod(httpMethod)
                .withUser(userAccountUtil)
                .expectedResponseCode(HttpStatus.FORBIDDEN)
                .build().sendRequest(mockMvc);
    }
}