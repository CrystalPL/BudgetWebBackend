package pl.crystalek.budgetweb.user.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import pl.crystalek.budgetweb.household.member.HouseholdMember;

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

    @Column(unique = true, nullable = false)
    String email;

    @Lazy
    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    boolean receiveUpdates;

    @OneToOne(mappedBy = "user")
    @Getter(onMethod_ = @Nullable)
    HouseholdMember householdMember;

    @OneToOne(mappedBy = "user")
    UserData userData;

    public User(final String email, final String password, final boolean receiveUpdates) {
        this.email = email;
        this.password = password;
        this.receiveUpdates = receiveUpdates;
    }

    public String getNickname() {
        return userData.getNickname();
    }

    public void setNickname(final String nickname) {
        userData.setNickname(nickname);
    }
}
