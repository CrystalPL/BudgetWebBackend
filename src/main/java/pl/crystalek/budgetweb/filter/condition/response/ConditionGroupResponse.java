package pl.crystalek.budgetweb.filter.condition.response;

import pl.crystalek.budgetweb.filter.condition.FilterLogicalOperator;

import java.util.List;

public record ConditionGroupResponse(
        long id,
        FilterLogicalOperator logicalOperatorBefore,
        List<ConditionResponse> conditions
) {
}
