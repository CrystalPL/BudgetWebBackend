package pl.crystalek.budgetweb.auth.controller.auth;

import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AuthControllerTest {
    MockMvc mockMvc;
    UserAccountUtil userAccountUtil;

    @ParameterizedTest
    @CsvSource({
            "'/auth/login', 'POST'",
            "'/auth/register', 'POST'",
            "'/auth/confirm', 'POST'",
    })
    void shouldReturnBadRequestWhenContentIsNull(final String path, final String httpMethod) throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();

        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path)
                        .cookie(cookie))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Request body is missing or empty"));
    }
}
