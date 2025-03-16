package pl.crystalek.budgetweb.auth.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class LogoutControllerTest extends BaseAccessControllerTest {
    ObjectMapper objectMapper;
    JdbcTemplate jdbcTemplate;
    EntityManager entityManager;

    @Autowired
    public LogoutControllerTest(final MockMvc mockMvc, final UserAccountUtil userAccountUtil, final ObjectMapper objectMapper, final JdbcTemplate jdbcTemplate, final EntityManager entityManager) {
        super(mockMvc, userAccountUtil);
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Override
    protected String[][] shouldDeniedAccessWithoutAccount() {
        return new String[][]{{"/auth/logout", "POST"}};
    }

    @Override
    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/auth/logout", "POST"}};
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();

        mockMvc.perform(post("/auth/logout").cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
