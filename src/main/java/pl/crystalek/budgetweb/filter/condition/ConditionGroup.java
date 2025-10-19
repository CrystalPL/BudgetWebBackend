package pl.crystalek.budgetweb.filter.condition;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.filter.AdvancedFilter;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "filter_condition_group")
public class ConditionGroup implements Cloneable {
    @Id
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(value = EnumType.STRING)
    FilterLogicalOperator logicalOperatorBefore;

    @Setter
    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false, updatable = false)
    AdvancedFilter advancedFilter;

    @OneToMany(mappedBy = "conditionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Condition> conditions;

    @Override
    public ConditionGroup clone() {
        try {
            final ConditionGroup conditionGroup = (ConditionGroup) super.clone();
            conditionGroup.id = null;
            conditionGroup.advancedFilter = null;
            conditionGroup.conditions = conditions.stream()
                    .map(condition -> cloneCondition(conditionGroup, condition))
                    .collect(Collectors.toSet());

            return conditionGroup;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

    private Condition cloneCondition(ConditionGroup conditionGroup, Condition condition) {
        final Condition clonedCondition = condition.clone();
        clonedCondition.setConditionGroup(conditionGroup);
        return clonedCondition;
    }
}
