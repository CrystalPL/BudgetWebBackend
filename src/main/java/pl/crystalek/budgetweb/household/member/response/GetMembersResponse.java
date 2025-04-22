package pl.crystalek.budgetweb.household.member.response;

public record GetMembersResponse(long userId, String username, HouseholdMemberRoleDTO role) {
}
