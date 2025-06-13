package pl.crystalek.budgetweb.confirmation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    Instant expireAt;

    @Enumerated(EnumType.STRING)
    ConfirmationTokenType confirmationTokenType;

    public ConfirmationToken(final User user, final Instant expireAt, final ConfirmationTokenType confirmationTokenType) {
        this.user = user;
        this.expireAt = expireAt;
        this.confirmationTokenType = confirmationTokenType;
    }
}
