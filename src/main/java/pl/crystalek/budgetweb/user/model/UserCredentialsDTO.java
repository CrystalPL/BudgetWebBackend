package pl.crystalek.budgetweb.user.model;

import pl.crystalek.budgetweb.user.UserRole;

public record UserCredentialsDTO(String email, String password, UserRole userRole) {
}
