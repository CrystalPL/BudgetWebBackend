package pl.crystalek.budgetweb.household.member.invite.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InviteHouseholdMemberRequest(
        @NotBlank(message = "MISSING_EMAIL")
        @Size(max = 255, message = "EMAIL_TOO_LONG")
        @Pattern(regexp = "^((?!\\.)[\\w\\-_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])$", message = "INVALID_EMAIL")
        String email
) {
}
