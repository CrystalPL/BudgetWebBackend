package pl.crystalek.budgetweb.filter.condition.model;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "filter_condition")
public class Condition implements Cloneable {
    @Id
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String fieldEnumName;

    String firstValueAsString;
    String secondValueAsString;
    Integer openParenthesis;
    Integer closeParenthesis;

    @Enumerated(EnumType.STRING)
    FilterLogicalOperator logicalOperatorBefore;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    FilterOperator operator;

    @Column(nullable = false)
    int position;

    @ManyToOne
    @Setter
    @JoinColumn(name = "condition_group_id", nullable = false, updatable = false)
    ConditionGroup conditionGroup;

    public Condition(final Long id, final String fieldEnumName, final String firstValueAsString, final String secondValueAsString,
                     final Integer openParenthesis, final Integer closeParenthesis, final FilterLogicalOperator logicalOperatorBefore,
                     final FilterOperator operator, final ConditionGroup conditionGroup) {
        this.id = id;
        this.fieldEnumName = fieldEnumName;
        this.firstValueAsString = firstValueAsString;
        this.secondValueAsString = secondValueAsString;
        this.openParenthesis = openParenthesis;
        this.closeParenthesis = closeParenthesis;
        this.logicalOperatorBefore = logicalOperatorBefore;
        this.operator = operator;
        this.conditionGroup = conditionGroup;
    }

    @Override
    public Condition clone() {
        try {
            final Condition clonedCondition = (Condition) super.clone();
            clonedCondition.id = null;
            clonedCondition.conditionGroup = null;
            return clonedCondition;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
}
