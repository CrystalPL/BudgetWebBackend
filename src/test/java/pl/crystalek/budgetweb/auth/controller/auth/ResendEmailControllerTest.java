package pl.crystalek.budgetweb.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.AccountConfirmationResendEmailResponseMessage;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.crystalek.budgetweb.utils.UserAccountUtil.getGuidFromByteArray;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ResendEmailControllerTest {
    MockMvc mockMvc;
    JdbcTemplate jdbcTemplate;
    UserAccountUtil userAccountUtil;
    EntityManager entityManager;
    ObjectMapper objectMapper;

    @Test
    void shouldResendEmailSuccessfully() throws Exception {
        final Cookie cookie = userAccountUtil.loginAndGetJwtToken();
        mockMvc.perform(post("/auth/resend-email").cookie(cookie))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnErrorWhenConfirmationTokenHasExpired() throws Exception {
        final Cookie cookie = userAccountUtil.loginAndGetJwtToken();

        jdbcTemplate.update("UPDATE confirmation_token SET expire_at = ?", Instant.now().minus(Duration.ofDays(30)));
        entityManager.clear();

        mockMvc.perform(post("/auth/resend-email").cookie(cookie))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(AccountConfirmationResendEmailResponseMessage.TOKEN_EXPIRED.name()))
                .andDo(print());
    }

    @Test
    void shouldReturnErrorWhenAccountIsAlreadyConfirmed() throws Exception {
        final Cookie cookie = userAccountUtil.loginAndGetJwtToken();
        final byte[] token = jdbcTemplate.queryForObject("SELECT id from confirmation_token", byte[].class);
        final UUID uuid = getGuidFromByteArray(token);

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(uuid.toString()))))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(post("/auth/resend-email").cookie(cookie))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(AccountConfirmationResendEmailResponseMessage.ACCOUNT_CONFIRMED.name()))
                .andDo(print());
    }
}
