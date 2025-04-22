package pl.crystalek.budgetweb.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import pl.crystalek.budgetweb.auth.controller.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.token.TokenProperties;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserAccountUtil {
    public static final RegisterRequest TESTING_USER = new RegisterRequest(
            "ValidUsername",
            "test@example.com",
            "test@example.com",
            "StrongPassword1!",
            "StrongPassword1!",
            true
    );
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    TokenProperties tokenProperties;
    JdbcTemplate jdbcTemplate;
    EntityManager entityManager;

    public static UUID getGuidFromByteArray(final byte[] bytes) {
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public UUID getConfirmationToken() {
        final byte[] token = jdbcTemplate.queryForObject("SELECT id from temporary_users", byte[].class);
        return getGuidFromByteArray(token);
    }

    public Cookie createConfirmedAccountAndGetJwtToken() throws Exception {
        register(TESTING_USER);
        final UUID uuid = getConfirmationToken();

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(uuid.toString()))))
                .andExpect(status().isOk())
                .andDo(print());

        return login();
    }

    public Cookie login() throws Exception {
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", false);

        return Arrays.stream(mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(loginRequest))
                                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")

                        )
                        .andReturn()
                        .getResponse()
                        .getCookies()).filter(x -> x.getName().equalsIgnoreCase(tokenProperties.getCookieName()))
                .findFirst().get();
    }

    public void register(final RegisterRequest registerRequest) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        entityManager.flush();
    }
}
