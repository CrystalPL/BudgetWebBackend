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
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.role.Role;
import pl.crystalek.budgetweb.user.User;

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

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    public HouseholdMember(final Household household, final User user) {
        this.household = household;
        this.user = user;
        this.role = household.getDefaultRole();
    }

    public HouseholdMember(final Household household, final User user, final Role role) {
        this.household = household;
        this.user = user;
        this.role = role;
    }
}
