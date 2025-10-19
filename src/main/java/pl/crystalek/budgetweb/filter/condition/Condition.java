package pl.crystalek.budgetweb.filter.condition;

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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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
    int openParenthesis;
    int closeParenthesis;

    @Enumerated(EnumType.STRING)
    FilterLogicalOperator logicalOperatorBefore;

    @Column(nullable = false)
    FilterOperator operator;

    @ManyToOne
    @Setter
    @JoinColumn(name = "condition_group_id", nullable = false, updatable = false)
    ConditionGroup conditionGroup;

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
