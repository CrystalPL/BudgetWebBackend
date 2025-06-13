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
import pl.crystalek.budgetweb.auth.controller.auth.request.RegisterRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FieldDefaults(level = AccessLevel.PRIVATE)
class RegisterRequestTest {
    Validator validator;

    @BeforeEach
    void setUp() {
        @Cleanup final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        final RegisterRequest request = new RegisterRequest(
                "ValidUsername",
                "valid.email@example.com",
                "valid.email@example.com",
                "StrongPassword1!",
                "StrongPassword1!",
                true
        );

        // When
        final Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request, RegisterRequest.RegisterRequestValidation.class);

        // Then
        assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @CsvSource({
            "' ', , , , , , 'MISSING_USERNAME'",
            ", , , , , , 'MISSING_USERNAME'",
            "'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', , , , , , 'TOO_LONG_USERNAME'",
            "'test', '', , , , , 'MISSING_EMAIL'",
            "'test', , , , , , 'MISSING_EMAIL'",
            "'test', 'invalid.email.com', , , , , 'INVALID_EMAIL'",
            "'test', 'invalid.email@', , , , , 'INVALID_EMAIL'",
            "'test', '@example.com', , , , , 'INVALID_EMAIL'",
            "'test', 'valid.email@domain', , , , , 'INVALID_EMAIL'",
            "'test', 'invalid!email@example.com', , , , , 'INVALID_EMAIL'",
            "'test', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@example.pl', , , , , 'EMAIL_TOO_LONG'",
            "'test', 'valid.email@example.com', , , , , 'MISSING_CONFIRM_EMAIL'",
            "'test', 'valid.email@example.com', '', , , , 'MISSING_CONFIRM_EMAIL'",
            "'test', 'valid.email@example.com', 'valid.emaail@example.com', , , , 'EMAIL_MISMATCH'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', , , , 'MISSING_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', '', , , 'MISSING_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'aaaaaaa', , , 'PASSWORD_TOO_SHORT'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', , , 'PASSWORD_TOO_LONG'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'strongpassword1!', , , 'MISSING_UPPERCASE'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'STRONGPASSWORD1!', , , 'MISSING_LOWERCASE'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword!', , , 'MISSING_NUMBER'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1', , , 'MISSING_SPECIAL_CHAR'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', , , 'MISSING_CONFIRM_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', '', , 'MISSING_CONFIRM_PASSWORD'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', 'DtrongPassword1!', , 'PASSWORD_MISMATCH'",
            "'test', 'valid.email@example.com', 'valid.email@example.com', 'StrongPassword1!', 'StrongPassword1!', , 'MISSING_RECEIVE_UPDATES'",
    })
    void shouldFailValidation(final String username, final String email, final String confirmEmail,
                              final String password, final String confirmPassword, final Boolean receiveUpdates,
                              final String errorMessage) {
        // Given
        final RegisterRequest request = new RegisterRequest(username, email, confirmEmail, password, confirmPassword, receiveUpdates);

        // When
        final Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request, RegisterRequest.RegisterRequestValidation.class);

        // Then
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(errorMessage)));
    }
}
