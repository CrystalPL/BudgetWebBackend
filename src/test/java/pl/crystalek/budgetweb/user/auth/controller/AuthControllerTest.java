package pl.crystalek.budgetweb.user.auth.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.utils.BaseTest;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

class AuthControllerTest extends BaseTest {

    @ParameterizedTest
    @CsvSource({
            "'/auth/confirm', 'POST'",
            "'/auth/login', 'POST'",
            "'/auth/password/recovery', 'POST'",
            "'/auth/password/reset', 'POST'",
            "'/auth/register', 'POST'",
            "'/auth/resend-email', 'POST'",
    })
    void shouldReturnBadRequestWhenContentIsNull(final String path, final String httpMethod) throws Exception {
        RequestHelper.builder()
                .path(path)
                .httpMethod(httpMethod)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseObject("Request body is missing or empty")
                .build().sendRequest(mockMvc);
    }
}
