package pl.crystalek.budgetweb.user.model;

import pl.crystalek.budgetweb.user.UserRole;

public record UserDTO(long id, String email, UserRole userRole) {
}
