package pl.crystalek.budgetweb.utils.request;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.intellij.lang.annotations.Language;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultMatcher;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Klasa typu builder służąca do konstruowania i konfigurowania żądań HTTP do testów integracyjnych z użyciem Spring MockMvc.
 * <p>
 * Upraszcza i standaryzuje testy kontrolerów poprzez ułatwienie ustawiania typu żądania, treści, nagłówków,
 * ciasteczek, oraz oczekiwań dotyczących odpowiedzi.
 *
 * <p>Przykład użycia:</p>
 * <pre>{@code
 * new RequestHelperBuilder()
 *     .path("/household/create")
 *     .withUser(userAccountUtil)
 *     .httpMethod(HttpMethod.POST)
 *     .content(new CreateHouseholdRequest("Nowe Gospodarstwo"))
 *     .expectedResponseCode(HttpStatus.OK)
 *     .expectedResponseMessage(CreateHouseholdResponseMessage.SUCCESS)
 *     .build()
 *     .sendRequest(mockMvc);
 * }</pre>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RequestHelperBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    HttpMethod httpMethod;
    String path;
    String contentType = MediaType.APPLICATION_JSON_VALUE;
    Object content;
    Map<String, Object> headers = new HashMap<>(Map.of("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"));
    Set<Cookie> cookies = new HashSet<>();
    Set<ResultMatcher> expects = new HashSet<>();
    Object responseObject;
    MockMultipartFile file;

    /**
     * Dodaje ciasteczko użytkownika testowego.
     * Użytkownik jest tworzony na podstawie stałej {@code UserAccountUtil.TESTING_USER}.
     *
     * @param userAccountUtil klasa pomocnicza do zarządzania kontami
     * @return instancja buildera
     */
    public RequestHelperBuilder withUser(UserAccountUtil userAccountUtil) {
        return cookie(userAccountUtil.createUserAndReturnAccessCookie(UserAccountUtil.TESTING_USER));
    }

    /**
     * Loguje wcześniej utworzonego użytkownika testowego i dodaje jego ciasteczko do żądania.
     *
     * @param userAccountUtil klasa pomocnicza do logowania użytkownika
     * @return instancja buildera
     */
    public RequestHelperBuilder loginUser(UserAccountUtil userAccountUtil) {
        return cookie(userAccountUtil.login(UserAccountUtil.LOGIN_TESTING_USER));
    }

    /**
     * Ustawia metodę HTTP (np. GET, POST, PUT, DELETE).
     *
     * @param httpMethod metoda HTTP
     * @return instancja buildera
     */
    public RequestHelperBuilder httpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    /**
     * Ustawia metodę HTTP (np. GET, POST, PUT, DELETE).
     *
     * @param httpMethod metoda HTTP
     * @return instancja buildera
     */
    public RequestHelperBuilder httpMethod(String httpMethod) {
        this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
        return this;
    }

    /**
     * Ustawia ścieżkę (endpoint) żądania.
     *
     * @param path ścieżka żądania, np. "/api/uzytkownicy"
     * @return instancja buildera
     */
    public RequestHelperBuilder path(@Language("http") String path) {
        this.path = path;
        return this;
    }

    /**
     * Ustawia typ treści (Content-Type) żądania.
     *
     * @param contentType typ MIME, np. "application/json"
     * @return instancja buildera
     */
    public RequestHelperBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Ustawia treść (body) żądania.
     *
     * @param content obiekt, który zostanie zserializowany do JSON
     * @return instancja buildera
     */
    public RequestHelperBuilder content(Object content) {
        this.content = content;
        return this;
    }

    /**
     * Ustawia nagłówki żądania, nadpisując domyślne.
     *
     * @param headers mapa nagłówków
     * @return instancja buildera
     */
    public RequestHelperBuilder headers(Map<String, Object> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Ustawia zestaw ciasteczek (cookies).
     *
     * @param cookies zbiór ciasteczek
     * @return instancja buildera
     */
    public RequestHelperBuilder cookies(Set<Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    /**
     * Dodaje pojedyncze ciasteczko do żądania.
     *
     * @param cookie ciasteczko do dodania
     * @return instancja buildera
     */
    public RequestHelperBuilder cookie(Cookie cookie) {
        cookies.add(cookie);
        return this;
    }

    /**
     * Dodaje oczekiwany rezultat (matcher) do listy oczekiwań.
     * Może to być np. status odpowiedzi, wartość pola JSON itp.
     *
     * @param expect matcher do dodania
     * @return instancja buildera
     */
    public RequestHelperBuilder expect(ResultMatcher expect) {
        expects.add(expect);
        return this;
    }

    /**
     * Zastępuje aktualną listę oczekiwanych matcherów.
     *
     * @param expects zbiór matcherów
     * @return instancja buildera
     */
    public RequestHelperBuilder expects(Set<ResultMatcher> expects) {
        this.expects = expects;
        return this;
    }

    /**
     * Oczekuje określonego kodu HTTP w odpowiedzi.
     *
     * @param expectedResponseCode oczekiwany kod HTTP
     * @return instancja buildera
     */
    public RequestHelperBuilder expectedResponseCode(HttpStatus expectedResponseCode) {
        return expect(status().is(expectedResponseCode.value()));
    }

    /**
     * Oczekuje, że pole "message" w odpowiedzi JSON będzie równe podanemu Stringowi.
     *
     * @param expectedResponseMessage oczekiwany komunikat jako String
     * @return instancja buildera
     */
    public RequestHelperBuilder expectedResponseMessage(String expectedResponseMessage) {
        return expect(jsonPath("$.message").value(expectedResponseMessage));
    }

    /**
     * Oczekuje, że pole "message" w odpowiedzi JSON będzie równe nazwie podanego enuma.
     *
     * @param expectedResponseMessage enum reprezentujący komunikat
     * @return instancja buildera
     */
    public RequestHelperBuilder expectedResponseMessage(Enum<?> expectedResponseMessage) {
        return expect(jsonPath("$.message").value(expectedResponseMessage.name()));
    }

    /**
     * Oczekuje, że odpowiedź będzie odpowiadała oczekiwanemu obiektowi (po deserializacji JSON).
     *
     * @param expectedObject obiekt oczekiwany w odpowiedzi
     * @return instancja buildera
     */
    public RequestHelperBuilder expectedResponseObject(Object expectedObject) {
        return expect(resultAction -> {
            final String responseJson = resultAction.getResponse().getContentAsString();
            try {
                this.responseObject = OBJECT_MAPPER.readValue(responseJson, expectedObject.getClass());
            } catch (JsonParseException | MismatchedInputException exception) {
                this.responseObject = responseJson;
            }

            assertEquals(expectedObject, responseObject);
        });
    }

    /**
     * Ustawia plik do przesłania w żądaniu multipart.
     * <p>
     * Plik zostanie dodany do żądania z nazwą parametru "file".
     *
     * @param file obiekt {@code File} reprezentujący plik, który ma być przesłany
     * @return instancja buildera
     * @throws RuntimeException jeśli nie można utworzyć {@code MockMultipartFile} z podanego pliku
     */

    public RequestHelperBuilder file(final File file, final MediaType contentType) {
        try {
            final InputStream inputStream = new FileInputStream(file);
            this.file = new MockMultipartFile("file", file.getName(), contentType.toString(), inputStream);
            return this;
        } catch (final IOException exception) {
            throw new RuntimeException("Cannot create MockMultipartFile object from file: " + file.getName(), exception);
        }

    }

    /**
     * Ustawia gotowy obiekt {@code MockMultipartFile} do przesłania w żądaniu multipart.
     *
     * @param file przygotowany obiekt {@code MockMultipartFile} do użycia w żądaniu
     * @return instancja buildera
     */

    public RequestHelperBuilder file(final MockMultipartFile file) {
        this.file = file;
        return this;
    }

    /**
     * Buduje obiekt {@link RequestHelper}, który można wykorzystać do wysłania żądania.
     *
     * @return skonfigurowany {@link RequestHelper}
     */
    public RequestHelper build() {
        return new RequestHelper(httpMethod, path, contentType, content, headers, cookies, expects, responseObject, file);
    }
}