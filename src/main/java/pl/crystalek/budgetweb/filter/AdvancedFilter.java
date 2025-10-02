package pl.crystalek.budgetweb.filter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvancedFilter {
    @Id
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String filterName;

    String description;

    @Column(nullable = false)
    boolean active;

    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    AdvancedFilterEntityType fieldType;

    @OneToMany(mappedBy = "advancedFilter", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ConditionGroup> conditionGroups;
}
