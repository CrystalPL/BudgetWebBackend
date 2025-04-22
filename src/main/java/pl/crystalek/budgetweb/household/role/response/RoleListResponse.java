package pl.crystalek.budgetweb.household.role.response;

public record RoleListResponse(long id, String name, String hexColor, boolean isDefault, boolean isOwnerRole) {
}
