package pl.crystalek.budgetweb.user.email;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.confirmation.ConfirmationToken;

import java.util.UUID;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ChangeEmail {
    @Id
    UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "confirmation_token_id")
    @MapsId
    ConfirmationToken confirmationToken;

    String newEmail;

    public ChangeEmail(final ConfirmationToken confirmationToken, final String newEmail) {
        this.confirmationToken = confirmationToken;
        this.newEmail = newEmail;
    }
}
