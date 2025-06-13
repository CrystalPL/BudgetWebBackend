package pl.crystalek.budgetweb.household.role.permission;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    HOUSEHOLD_DELETE,
    HOUSEHOLD_CHANGE_NAME,
    HOUSEHOLD_LOGS,

    HOUSEHOLD_MEMBER_INVITE,
    HOUSEHOLD_MEMBER_CANCEL_INVITATION,
    HOUSEHOLD_MEMBER_DELETE,

    ROLE_CHANGE,
    ROLE_CREATE,
    ROLE_DELETE,
    ROLE_EDIT,
    ROLE_MAKE_DEFAULT,

    ROLE_PERMISSIONS_VIEW,
    ROLE_PERMISSIONS_EDIT,

    CATEGORY_CREATE,
    CATEGORY_DELETE,
    CATEGORY_EDIT;

    @Override
    public String getAuthority() {
        return name();
    }
}
