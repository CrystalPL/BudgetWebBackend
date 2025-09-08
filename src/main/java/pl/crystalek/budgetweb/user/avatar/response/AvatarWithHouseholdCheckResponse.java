package pl.crystalek.budgetweb.user.avatar.response;

import pl.crystalek.budgetweb.user.avatar.Avatar;

public record AvatarWithHouseholdCheckResponse(
        Avatar avatar,
        boolean isSameHousehold
) {
}
