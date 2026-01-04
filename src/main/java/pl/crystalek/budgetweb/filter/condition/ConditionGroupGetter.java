package pl.crystalek.budgetweb.filter.condition;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.exception.BudgetAppException;
import pl.crystalek.budgetweb.filter.AdvancedFilter;
import pl.crystalek.budgetweb.filter.AdvancedFilterFacade;
import pl.crystalek.budgetweb.filter.condition.model.Condition;
import pl.crystalek.budgetweb.filter.condition.model.ConditionGroup;
import pl.crystalek.budgetweb.filter.condition.response.ConditionGroupGetterResponse;
import pl.crystalek.budgetweb.filter.condition.response.ConditionGroupResponse;
import pl.crystalek.budgetweb.filter.condition.response.ConditionResponse;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ConditionGroupGetter {
    AdvancedFilterFacade advancedFilterFacade;

    public List<ConditionGroupResponse> getConditionGroups(final long advancedFilterId, final long requesterId) {
        final Optional<AdvancedFilter> advancedFilterOptional = advancedFilterFacade.getAdvancedFilter(advancedFilterId, requesterId);
        if (advancedFilterOptional.isEmpty()) {
            throw new BudgetAppException(ConditionGroupGetterResponse.ADVANCED_FILTER_NOT_FOUND);
        }


        final AdvancedFilter advancedFilter = advancedFilterOptional.get();
        return advancedFilter.getConditionGroups().stream()
                .map(conditionGroup -> buildConditionGroupResponse(conditionGroup, advancedFilter))
                .toList();
    }

    private ConditionGroupResponse buildConditionGroupResponse(final ConditionGroup conditionGroup, final AdvancedFilter advancedFilter) {
        return new ConditionGroupResponse(
                conditionGroup.getId(),
                conditionGroup.getLogicalOperatorBefore(),
                buildConditionListResponse(conditionGroup.getConditions(), advancedFilter)
        );
    }

    private List<ConditionResponse> buildConditionListResponse(final List<Condition> conditions, final AdvancedFilter advancedFilter) {
        return conditions.stream()
                .map(condition -> buildConditionResponse(condition, advancedFilter))
                .toList();
    }

    private ConditionResponse buildConditionResponse(final Condition condition, final AdvancedFilter advancedFilter) {
        return new ConditionResponse(
                condition.getId(),
                advancedFilter.parseField(condition.getFieldEnumName()).getFieldName(),
                condition.getFirstValueAsString(),
                condition.getSecondValueAsString(),
                condition.getOpenParenthesis(),
                condition.getCloseParenthesis(),
                condition.getLogicalOperatorBefore(),
                condition.getOperator()
        );
    }
}
