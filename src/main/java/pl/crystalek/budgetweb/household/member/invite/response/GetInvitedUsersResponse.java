package pl.crystalek.budgetweb.household.member.invite.response;

import java.time.Instant;

public record GetInvitedUsersResponse(long userId, String email, Instant invitedTime) {
}
