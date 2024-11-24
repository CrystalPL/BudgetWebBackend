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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccessControllerTest {
    MockMvc mockMvc;
    UserAccountUtil userAccountUtil;

    @ParameterizedTest
    @CsvSource({
            "/auth/confirm",
            "/auth/login",
            "/auth/register",
            "/auth/password/recovery",
            "/auth/password/reset",
            "/account/confirm-change-email/**"
    })
    public void shouldAllowAccessToNoSecuredEndpoint(final String path) throws Exception {
        mockMvc.perform(post(path))
                .andExpect(result -> assertNotEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus()));
    }

    @ParameterizedTest
    @CsvSource({
            "'/account/avatar', 'POST'",
            "'/account/avatar', 'GET'",
            "'/account/change-email', 'POST'",
            "'/account/change-nickname', 'POST'",
            "'/account/change-password', 'POST'",
            "'/account/email-changing-wait-to-confirm', 'GET'",
            "'/account/info', 'GET'",
            "'/auth/logout', 'POST'",
            "'/auth/resend-email', 'POST'",
            "'/auth/verify', 'POST'",
            "'/household/create', 'POST'",
            "'/household/members/invite', 'POST'"
    })
    public void shouldDeniedAccessToSecuredEndpoint(final String path, final String httpMethod) throws Exception {
        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @CsvSource({
            "'/account/avatar', 'POST'",
            "'/account/avatar', 'GET'",
            "'/account/change-email', 'POST'",
            "'/account/change-nickname', 'POST'",
            "'/account/change-password', 'POST'",
            "'/account/email-changing-wait-to-confirm', 'GET'",
            "'/account/info', 'GET'",
            "'/auth/logout', 'POST'",
            "'/auth/verify', 'POST'",
            "'/household/create', 'POST'",
            "'/household/members/invite', 'POST'"
    })
    public void shouldDeniedAccessToSecuredEndpointWithGuestRole(final String path, final String httpMethod) throws Exception {
        final Cookie cookie = userAccountUtil.loginAndGetJwtToken();

        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path).cookie(cookie))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
            "'/account/avatar', 'POST'",
            "'/account/avatar', 'GET'",
            "'/account/change-email', 'POST'",
            "'/account/change-nickname', 'POST'",
            "'/account/change-password', 'POST'",
            "'/account/email-changing-wait-to-confirm', 'GET'",
            "'/account/info', 'GET'",
            "'/auth/logout', 'POST'",
            "'/auth/resend-email', 'POST'",
            "'/auth/verify', 'POST'",
            "'/household/create', 'POST'",
            "'/household/members/invite', 'POST'"
    })
    public void shouldAllowAccessToRoleSecuredEndpointWithUserRole(final String path, final String httpMethod) throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();
        mockMvc.perform(request(HttpMethod.valueOf(httpMethod), path).cookie(cookie))
                .andExpect(result -> assertNotEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus()))
                .andExpect(result -> assertNotEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus()));
    }
}
