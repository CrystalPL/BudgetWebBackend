package pl.crystalek.budgetweb.user.data;

public enum UserDataValidationResult {
    MISSING_USERNAME,
    TOO_LONG_USERNAME,

    MISSING_EMAIL,
    INVALID_EMAIL,
    TOO_LONG_EMAIL,

    MISSING_CONFIRM_EMAIL,

    EMAIL_MISMATCH,

    MISSING_PASSWORD,

    MISSING_CONFIRM_PASSWORD,

    PASSWORD_MISMATCH,

    OK
}
