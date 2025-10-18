package pl.crystalek.budgetweb.household;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.household.member.invite.HouseholdInviteMember;
import pl.crystalek.budgetweb.household.role.Role;
import pl.crystalek.budgetweb.receipt.Receipt;
import pl.crystalek.budgetweb.receipt.category.Category;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Household {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    Instant creationTime;

    @OneToOne
    @Setter
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    User owner;

    @OneToOne
    @JoinColumn(name = "default_role_id")
    @Setter
    Role defaultRole;

    @OneToOne
    @JoinColumn(name = "owner_role_id")
    @Setter
    Role ownerRole;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Role> roles;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Category> categories;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<HouseholdMember> members;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<HouseholdInviteMember> inviteMembers;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Receipt> receipts;

    public Household(final String name, final Instant creationTime, final User owner) {
        this.name = name;
        this.creationTime = creationTime;
        this.owner = owner;
    }
}
