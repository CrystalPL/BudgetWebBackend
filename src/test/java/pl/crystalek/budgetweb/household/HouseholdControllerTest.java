package pl.crystalek.budgetweb.household;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.household.request.CreateHouseholdRequest;
import pl.crystalek.budgetweb.household.response.CreateHouseholdResponseMessage;
import pl.crystalek.budgetweb.utils.UserAccountUtil;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HouseholdControllerTest {
    MockMvc mockMvc;
    UserAccountUtil userAccountUtil;
    EntityManager entityManager;
    @NonFinal Validator validator;

    @BeforeEach
    void setUp() {
        @Cleanup final ValidatorFactory factory = jakarta.validation.Validation.buildDefaultValidatorFactory();
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

        final Set<ConstraintViolation<CreateHouseholdRequest>> violations = validator.validate(createHouseholdRequest, CreateHouseholdRequest.Validation.class);

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
        final CreateHouseholdRequest createHouseholdRequest = new CreateHouseholdRequest(name);

        RequestHelper.builder()
                .path("/household/create")
                .withUser(userAccountUtil)
                .httpMethod(HttpMethod.POST)
                .content(createHouseholdRequest)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(errorMessage)
                .build()
                .sendRequest(mockMvc);
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
        final CreateHouseholdRequest createHouseholdRequest = new CreateHouseholdRequest(name);

        RequestHelper.builder()
                .path("/household/create")
                .withUser(userAccountUtil)
                .httpMethod(HttpMethod.POST)
                .content(createHouseholdRequest)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(CreateHouseholdResponseMessage.SUCCESS)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnUserIsAlreadyOwnerWhenUserOwnsAnotherHousehold() throws Exception {
        final CreateHouseholdRequest request1 = new CreateHouseholdRequest("Nowe Gospodarstwo");
        final CreateHouseholdRequest request2 = new CreateHouseholdRequest("Nowe Gospodarstwo2");

        RequestHelper.builder()
                .path("/household/create")
                .httpMethod(HttpMethod.POST)
                .withUser(userAccountUtil)
                .content(request1)
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(CreateHouseholdResponseMessage.SUCCESS)
                .build()
                .sendRequest(mockMvc);

        entityManager.clear();

        RequestHelper.builder()
                .path("/household/create")
                .httpMethod(HttpMethod.POST)
                .loginUser(userAccountUtil)
                .content(request2)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(CreateHouseholdResponseMessage.USER_IS_ALREADY_OWNER)
                .build()
                .sendRequest(mockMvc);
    }

    @Test
    void shouldReturnBadRequestWhenContentIsNull() throws Exception {
        RequestHelper.builder()
                .path("/household/create")
                .httpMethod(HttpMethod.POST)
                .withUser(userAccountUtil)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .content("Request body is missing or empty")
                .build()
                .sendRequest(mockMvc);
    }

    //TODO dodac sytuacje, gdy użytkownik jest czlonkiem jakiegoś gospodarstwa domowego, a chce utworzyć nowe
}
