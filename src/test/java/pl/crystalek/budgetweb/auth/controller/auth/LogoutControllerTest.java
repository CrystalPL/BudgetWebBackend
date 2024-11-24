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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LogoutControllerTest {
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    UserAccountUtil userAccountUtil;
    JdbcTemplate jdbcTemplate;
    EntityManager entityManager;

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();

        mockMvc.perform(post("/auth/logout").cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
