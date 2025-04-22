package pl.crystalek.budgetweb.auth.controller.auth.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.crystalek.budgetweb.auth.controller.auth.request.LoginRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FieldDefaults(level = AccessLevel.PRIVATE)
class LoginRequestTest {
    Validator validator;

    @BeforeEach
    void setUp() {
        @Cleanup final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "'', , , 'MISSING_EMAIL'",
            ", , , 'MISSING_EMAIL'",
            "'test@example.com', '', , 'MISSING_PASSWORD'",
            "'test@example.com', , , 'MISSING_PASSWORD'",
            "'test@example.com', 'StrongPassword1!', , 'MISSING_REMEMBER_ME'",
    })
    void shouldFailValidation(final String email, final String password, final Boolean rememberMe, final String errorMessage) {
        // Given
        final LoginRequest loginRequest = new LoginRequest(email, password, rememberMe);

        // When
        final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest, LoginRequest.LoginRequestValidation.class);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(errorMessage)));
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        // Given
        final LoginRequest loginRequest = new LoginRequest("test@example.com", "StrongPassword1!", true);

        // When
        final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        // Then
        assertEquals(0, violations.size());
    }
}
