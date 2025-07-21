package pl.crystalek.budgetweb.token.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.user.auth.device.DeviceInfo;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Embedded
    DeviceInfo deviceInfo;

    Instant expireAt;
    boolean rememberMe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    public RefreshToken(final DeviceInfo deviceInfo, final Instant expireAt, final boolean rememberMe, final User user) {
        this.deviceInfo = deviceInfo;
        this.expireAt = expireAt;
        this.rememberMe = rememberMe;
        this.user = user;
    }
}
