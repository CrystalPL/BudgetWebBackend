package pl.crystalek.budgetweb.household.role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.household.role.permission.RolePermission;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "role", uniqueConstraints = {@UniqueConstraint(columnNames = {"household_id", "name"})})
public class Role {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "household_id")
    Household household;

    @Setter
    String name;

    @Setter
    String color;

    Instant creationTime;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    Set<RolePermission> permissionSet;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<HouseholdMember> membersWithRole;

    public Role(final Household household, final String name, final String color, final Instant creationTime) {
        this.household = household;
        this.name = name;
        this.color = color;
        this.creationTime = creationTime;
    }

}
