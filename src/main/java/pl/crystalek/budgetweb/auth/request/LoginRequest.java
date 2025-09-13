package pl.crystalek.budgetweb.auth.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank(message = "MISSING_EMAIL", groups = ValidationGroups.Email.class)
        String email,

        @NotBlank(message = "MISSING_PASSWORD", groups = ValidationGroups.Password.class)
        String password,

        @NotNull(message = "MISSING_REMEMBER_ME", groups = ValidationGroups.RememberMe.class)
        Boolean rememberMe
) {
    @GroupSequence({ValidationGroups.Email.class, ValidationGroups.Password.class, ValidationGroups.RememberMe.class})
    public interface LoginRequestValidation {}

    private interface ValidationGroups {
        interface Email {}

        interface Password {}

        interface RememberMe {}
    }
}