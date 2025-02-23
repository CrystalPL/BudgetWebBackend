package pl.crystalek.budgetweb.household.member.invite.model;

import java.time.Instant;

public record GetInvitedUsersResponse(long userId, String email, Instant invitedTime) {
}
