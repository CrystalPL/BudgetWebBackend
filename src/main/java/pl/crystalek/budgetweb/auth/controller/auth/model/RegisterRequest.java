package pl.crystalek.budgetweb.auth.controller.auth.model;

public record RegisterRequest(String username, String email, String confirmEmail, String password,
                              String confirmPassword, boolean receiveUpdates) {
}
