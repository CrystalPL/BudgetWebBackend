package pl.crystalek.budgetweb.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum AuthenticateStatus {
    SUCCESS(HttpStatus.OK),
    FAILURE_BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    FAILURE_USER_NOT_EXIST(HttpStatus.NOT_FOUND),
    FAILURE_ACCOUNT_NOT_CONFIRMED(HttpStatus.FORBIDDEN);

    HttpStatus httpStatus;
}
