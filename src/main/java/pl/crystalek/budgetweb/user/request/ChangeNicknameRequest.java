package pl.crystalek.budgetweb.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeNicknameRequest(
        @NotBlank(message = "MISSING_USERNAME")
        @Size(max = 64, message = "TOO_LONG_USERNAME")
        String nickname
) {
}
