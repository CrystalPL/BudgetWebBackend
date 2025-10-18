package pl.crystalek.budgetweb.household.member.invite.request;

import pl.crystalek.budgetweb.user.validator.email.ValidEmail;

public record InviteHouseholdMemberRequest(
        @ValidEmail
        String email
) {
}
