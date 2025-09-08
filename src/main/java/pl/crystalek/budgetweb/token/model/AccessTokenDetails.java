package pl.crystalek.budgetweb.token.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessTokenDetails {
    long userId;
    long refreshTokenId;
    @Builder.Default
    Instant expiresAt = Instant.EPOCH;
    @Builder.Default
    boolean verified = true;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
