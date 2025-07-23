package pl.crystalek.budgetweb.user.avatar;

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
import pl.crystalek.budgetweb.user.model.User;

import java.util.UUID;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
    String extension;

    public Avatar(final User user, final String extension) {
        this.user = user;
        this.extension = extension;
    }
}
