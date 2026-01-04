package pl.crystalek.budgetweb.filter.condition.response;

import pl.crystalek.budgetweb.filter.condition.model.FilterLogicalOperator;
import pl.crystalek.budgetweb.filter.condition.model.FilterOperator;

public record ConditionResponse(
        long id,
        String fieldName,
        String firstValue,
        String secondValue,
        int openParenthesis,
        int closeParenthesis,
        FilterLogicalOperator logicalOperatorBefore,
        FilterOperator operator
) {
}
