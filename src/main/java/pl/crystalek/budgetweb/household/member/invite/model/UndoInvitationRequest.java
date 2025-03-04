package pl.crystalek.budgetweb.household.member.invite.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UndoInvitationRequest(
        @NotNull(message = "MISSING_MEMBER_ID")
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT")
        String memberId
) {

    public Long getMemberId() {
        return Long.parseLong(memberId);
    }
}
