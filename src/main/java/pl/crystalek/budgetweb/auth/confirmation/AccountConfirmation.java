package pl.crystalek.budgetweb.auth.confirmation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.user.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AccountConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
    Instant expireAt;

    public AccountConfirmation(final User user, final Instant expireAt) {
        this.user = user;
        this.expireAt = expireAt;
    }
}
