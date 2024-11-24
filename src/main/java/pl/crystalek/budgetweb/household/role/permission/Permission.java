package pl.crystalek.budgetweb.household.role.permission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Permission {
    HOUSEHOLD_INVITE_MEMBER("household.member.invite"),
    HOUSEHOLD_UNDO_INVITE_MEMBER("household.member.invite.undo");

    String permission;
}
