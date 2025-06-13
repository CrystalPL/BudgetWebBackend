package pl.crystalek.budgetweb.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.crystalek.budgetweb.auth.controller.auth.request.AccountConfirmationRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.token.TokenProperties;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserData;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

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
    public static final LoginRequest LOGIN_TESTING_USER = new LoginRequest("test@example.com", "StrongPassword1!", false);
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    TokenProperties tokenProperties;
    EntityManager entityManager;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UUID getConfirmationToken() {
        return (UUID) entityManager.createQuery("select tu.id from TemporaryUser tu").getSingleResult();
    }

    public Cookie createConfirmedAccountAndGetJwtToken() throws Exception {
        register(TESTING_USER);
        final UUID uuid = getConfirmationToken();

        mockMvc.perform(post("/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new AccountConfirmationRequest(uuid.toString()))))
                .andExpect(status().isOk())
                .andDo(print());

        return login(LOGIN_TESTING_USER);
    }

    public void register(final RegisterRequest registerRequest) {
        final User user = new User(registerRequest.email(), bCryptPasswordEncoder.encode(registerRequest.password()), registerRequest.receiveUpdates());
        final UserData userData = new UserData(user, registerRequest.username());

        entityManager.persist(user);
        entityManager.persist(userData);
        entityManager.flush();
    }

    @SneakyThrows
    public Cookie createUserAndReturnAccessCookie(final RegisterRequest registerRequest) {
        final User user = new User(registerRequest.email(), bCryptPasswordEncoder.encode(registerRequest.password()), registerRequest.receiveUpdates());
        final UserData userData = new UserData(user, registerRequest.username());

        entityManager.persist(user);
        entityManager.persist(userData);
        entityManager.flush();

        return login(LOGIN_TESTING_USER);
    }

    @SneakyThrows
    public Cookie login(final LoginRequest loginRequest) {
        final ResultActions actions = RequestHelper.builder()
                .path("/auth/login")
                .content(loginRequest)
                .expectedResponseCode(HttpStatus.OK)
                .httpMethod(HttpMethod.POST)
                .build().sendRequest(mockMvc).getResultActions();

        return Arrays.stream(actions.andReturn()
                        .getResponse()
                        .getCookies()).filter(x -> x.getName().equalsIgnoreCase(tokenProperties.getCookieName()))
                .findFirst().get();
    }
}
