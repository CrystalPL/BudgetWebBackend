package pl.crystalek.budgetweb.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.auth.confirmation.AccountConfirmation;
import pl.crystalek.budgetweb.auth.token.model.RefreshToken;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(unique = true)
    String email;

    String password;
    String nickname;

    @OneToMany(mappedBy = "user")
    Set<RefreshToken> refreshTokens = new HashSet<>();

    @OneToOne(mappedBy = "user")
    AccountConfirmation accountConfirmationVerification;

//    @OneToOne(mappedBy = "user")
//    PasswordRecovery passwordRecovery;

    @Enumerated(EnumType.STRING)
    UserRole userRole = UserRole.GUEST;
    boolean receiveUpdates;

    public User(final String email, final String password, final String nickname, final boolean receiveUpdates) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.receiveUpdates = receiveUpdates;
    }
}
