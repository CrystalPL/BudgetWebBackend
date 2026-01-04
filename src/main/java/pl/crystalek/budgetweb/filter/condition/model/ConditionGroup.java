package pl.crystalek.budgetweb.filter.condition.model;

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
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.filter.AdvancedFilter;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "filter_condition_group")
public class ConditionGroup implements Cloneable {
    @Id
    @Getter
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Getter
    @Enumerated(value = EnumType.STRING)
    FilterLogicalOperator logicalOperatorBefore;

    @Column(nullable = false)
    int position;

    @Setter
    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false, updatable = false)
    AdvancedFilter advancedFilter;

    @Getter
    @Setter
    @OneToMany(mappedBy = "conditionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "position")
    List<Condition> conditions;

    public ConditionGroup(final Long id, final FilterLogicalOperator logicalOperatorBefore, final AdvancedFilter advancedFilter) {
        this.id = id;
        this.logicalOperatorBefore = logicalOperatorBefore;
        this.advancedFilter = advancedFilter;
    }

    @Override
    public ConditionGroup clone() {
        try {
            final ConditionGroup conditionGroup = (ConditionGroup) super.clone();
            conditionGroup.id = null;
            conditionGroup.advancedFilter = null;
            conditionGroup.conditions = conditions.stream()
                    .map(condition -> cloneCondition(conditionGroup, condition))
                    .toList();

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
