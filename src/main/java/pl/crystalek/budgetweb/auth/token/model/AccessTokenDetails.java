package pl.crystalek.budgetweb.auth.token.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.user.UserRole;

import java.time.Instant;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessTokenDetails {
    long userId;
    long refreshTokenId;
    UserRole role;
    Instant expiresAt;
    @Builder.Default
    boolean verified = true;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}