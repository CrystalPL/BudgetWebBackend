package pl.crystalek.budgetweb.user;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN, USER, GUEST;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

    public static UserRole getRoleByString(final String role) {
        return valueOf(role.toUpperCase());
    }

    public boolean equals(final GrantedAuthority authority) {
        return getAuthority().equals(authority.getAuthority());
    }
}