package pl.crystalek.budgetweb.filter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.crystalek.budgetweb.filter.condition.model.ConditionGroup;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class AdvancedFilter implements Cloneable {
    @Id
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    String filterName;

    String description;

    @Column(nullable = false)
    boolean active;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    User user;

    @Getter
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    AdvancedFilterEntityType fieldType;

    @Getter
    @OneToMany(mappedBy = "advancedFilter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "position")
    List<ConditionGroup> conditionGroups;

    public AdvancedFilter(final String filterName, final String description, final User user, final AdvancedFilterEntityType fieldType) {
        this.filterName = filterName;
        this.description = description;
        this.user = user;
        this.fieldType = fieldType;
    }

    public void update(final String filterName, final String description) {
        this.filterName = filterName;
        this.description = description;
    }

    public AdvancedFilterField parseField(final String fieldEnumName) {
        return fieldType.fromEnumName(fieldEnumName).get();
    }

    @Override
    public AdvancedFilter clone() {
        try {
            final AdvancedFilter advancedFilter = (AdvancedFilter) super.clone();
            advancedFilter.id = null;
            advancedFilter.conditionGroups = conditionGroups.stream()
                    .map(conditionGroup -> cloneConditionGroup(conditionGroup, advancedFilter))
                    .toList();

            return advancedFilter;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

    private ConditionGroup cloneConditionGroup(final ConditionGroup conditionGroup, final AdvancedFilter advancedFilter) {
        final ConditionGroup clonedConditionGroup = conditionGroup.clone();
        clonedConditionGroup.setAdvancedFilter(advancedFilter);
        return clonedConditionGroup;
    }
}
