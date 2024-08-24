package pl.crystalek.budgetweb.auth.controller.auth.model;

public record LoginRequest(String email, String password, boolean rememberMe) {
}
