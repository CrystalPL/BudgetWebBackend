package pl.crystalek.budgetweb.household;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.household.model.CreateHouseholdRequest;
import pl.crystalek.budgetweb.household.model.CreateHouseholdResponseMessage;
import pl.crystalek.budgetweb.utils.UserAccountUtil;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HouseholdControllerTest {
    ObjectMapper objectMapper;
    MockMvc mockMvc;
    UserAccountUtil userAccountUtil;
    EntityManager entityManager;
    @NonFinal Validator validator;

    @BeforeEach
    void setUp() {
        @Cleanup final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "'', MISSING_NAME",
            ", MISSING_NAME",
            "' ', MISSING_NAME",
            "'A', NAME_TOO_SHORT",
            "'abcdefghijklmnopqrstuvwxyzabcdefg', NAME_TOO_LONG"
    })
    void shouldReturnBadRequest(final String name, final String errorMessage) {
        final CreateHouseholdRequest createHouseholdRequest = new CreateHouseholdRequest(name);

        final Set<ConstraintViolation<CreateHouseholdRequest>> violations = validator.validate(createHouseholdRequest, CreateHouseholdRequest.CreateHouseholdRequestValidation.class);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(errorMessage)));
    }

    @ParameterizedTest
    @CsvSource({
            "'', MISSING_NAME",
            ", MISSING_NAME",
            "' ', MISSING_NAME",
            "'A', NAME_TOO_SHORT",
            "'abcdefghijklmnopqrstuvwxyzabcdefg', NAME_TOO_LONG"
    })
    void shouldReturnBadRequestWhenHouseholdNameIsInvalid(final String name, final String errorMessage) throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();

        final CreateHouseholdRequest createHouseholdRequest = new CreateHouseholdRequest(name);

        mockMvc.perform(post("/household/create")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createHouseholdRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "'Potęga'",
            "'Gospodarstwo domowe v1'",
            "'Gospodarstwo domowe v2'",
            "'Te'",
            "'Ted'",
    })
    void shouldCreateHouseholdWhenNameIsValid(final String name) throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();

        final CreateHouseholdRequest createHouseholdRequest = new CreateHouseholdRequest(name);

        mockMvc.perform(post("/household/create")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createHouseholdRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(CreateHouseholdResponseMessage.SUCCESS.name()));
    }

    @Test
    void shouldReturnUserIsAlreadyOwnerWhenUserOwnsAnotherHousehold() throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();

        final CreateHouseholdRequest request = new CreateHouseholdRequest("Nowe Gospodarstwo");
        final CreateHouseholdRequest request2 = new CreateHouseholdRequest("Nowe Gospodarstwo2");

        mockMvc.perform(post("/household/create")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(CreateHouseholdResponseMessage.SUCCESS.name()));

        entityManager.clear();

        mockMvc.perform(post("/household/create")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CreateHouseholdResponseMessage.USER_IS_ALREADY_OWNER.name()));
    }

    @Test
    void shouldReturnBadRequestWhenContentIsNull() throws Exception {
        final Cookie cookie = userAccountUtil.createConfirmedAccountAndGetJwtToken();


        mockMvc.perform(post("/household/create")
                        .cookie(cookie))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Request body is missing or empty"));
    }

    //TODO dodac sytuacje, gdy użytkownik jest czlonkiem jakiegoś gospodarstwa domowego, a chce utworzyć nowe
}
