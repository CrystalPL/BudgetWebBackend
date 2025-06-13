package pl.crystalek.budgetweb.household.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.role.Role;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HouseholdMember {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    Household household;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Setter
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @Column(nullable = false)
    Instant joinDate;

    public HouseholdMember(final Household household, final User user, final Role role, final Instant joinDate) {
        this.household = household;
        this.user = user;
        this.role = role;
        this.joinDate = joinDate;
    }
}
