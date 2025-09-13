package pl.crystalek.budgetweb.user.profile.avatar.response;

import pl.crystalek.budgetweb.user.profile.avatar.Avatar;

public record AvatarWithHouseholdCheckResponse(
        Avatar avatar,
        boolean isSameHousehold
) {
}
