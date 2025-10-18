package pl.crystalek.budgetweb.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pl.crystalek.budgetweb.user.validator.UsernameValidationConstraints;

public record ChangeNicknameRequest(
        @NotBlank(message = "MISSING_USERNAME")
        @Size(max = UsernameValidationConstraints.USERNAME_MAX_LENGTH, message = "TOO_LONG_USERNAME")
        String nickname
) {
}
