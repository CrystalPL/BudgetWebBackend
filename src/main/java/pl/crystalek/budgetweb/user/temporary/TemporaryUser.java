package pl.crystalek.budgetweb.user.temporary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "temporary_users")
@NoArgsConstructor
public class TemporaryUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String nickname;

    @Column(nullable = false)
    boolean receiveUpdates;

    @Column(nullable = false)
    Instant expireAt;

    public TemporaryUser(final String email, final String password, final String nickname, final boolean receiveUpdates, final Instant expireAt) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.receiveUpdates = receiveUpdates;
        this.expireAt = expireAt;
    }
}
