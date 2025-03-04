package pl.crystalek.budgetweb.household.member.model;

public record GetMembersResponse(long userId, String username, HouseholdMemberRoleDTO role) {
}
