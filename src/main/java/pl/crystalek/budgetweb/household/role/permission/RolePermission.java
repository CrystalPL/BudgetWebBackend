package pl.crystalek.budgetweb.household.role.permission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.role.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "role_permission", uniqueConstraints = {@UniqueConstraint(columnNames = {"role_id", "permission_name"})})
public class RolePermission {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @Enumerated(EnumType.STRING)
    Permission permissionName;

    public RolePermission(final Role role, final Permission permissionName) {
        this.role = role;
        this.permissionName = permissionName;
    }
}
