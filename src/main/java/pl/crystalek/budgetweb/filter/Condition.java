package pl.crystalek.budgetweb.filter;

import com.jayway.jsonpath.internal.filter.LogicalOperator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Condition {
    @Id
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

//    AdvancedFilterField advancedFilterField; IDENTYFIKACJA JAKIEGO POLA MA DOTYCZYĆ WALIDACJA

    int openParenthesis;
    int closeParenthesis;
    LogicalOperator logicalOperatorBefore;

    @Column(nullable = false)
    FilterOperator operator;
}
