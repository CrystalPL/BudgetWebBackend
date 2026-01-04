package pl.crystalek.budgetweb.filter.condition;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.filter.AdvancedFilter;
import pl.crystalek.budgetweb.filter.condition.model.Condition;
import pl.crystalek.budgetweb.filter.condition.model.ConditionGroup;
import pl.crystalek.budgetweb.filter.condition.model.FilterLogicalOperator;
import pl.crystalek.budgetweb.filter.condition.model.FilterOperator;
import pl.crystalek.budgetweb.filter.condition.request.SaveConditionGroupRequest;
import pl.crystalek.budgetweb.filter.condition.request.SaveConditionRequest;
import pl.crystalek.budgetweb.filter.condition.request.SaveFilterConditionRequest;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ConditionAssembler {
    AdvancedFilter advancedFilter;
    SaveFilterConditionRequest saveFilterConditionRequest;

    public AdvancedFilter createAdvancedFilter() {
        final List<ConditionGroup> conditionGroups = saveFilterConditionRequest.conditionGroups().stream()
                .map(this::createConditionGroup)
                .toList();

        advancedFilter.getConditionGroups().clear();
        advancedFilter.getConditionGroups().addAll(conditionGroups);

        return advancedFilter;
    }

    private ConditionGroup createConditionGroup(final SaveConditionGroupRequest conditionGroupRequest) {
        final Long conditionGroupId = conditionGroupRequest.conditionGroupId();
        final FilterLogicalOperator filterLogicalOperator = conditionGroupRequest.logicalOperatorBefore();
        final ConditionGroup conditionGroup = new ConditionGroup(conditionGroupId, filterLogicalOperator, advancedFilter);

        final List<Condition> newConditions = conditionGroupRequest.conditions().stream()
                .map(conditionRequest -> createCondition(conditionRequest, conditionGroup))
                .toList();

        conditionGroup.setConditions(newConditions);

        return conditionGroup;
    }

    private Condition createCondition(final SaveConditionRequest saveConditionRequest, final ConditionGroup conditionGroup) {
        final Long conditionId = saveConditionRequest.conditionId();
        final Integer openParenthesisNumber = saveConditionRequest.openParenthesisNumber();
        final FilterLogicalOperator logicalOperatorBefore = saveConditionRequest.logicalOperatorBefore();
        final FilterOperator filterOperator = saveConditionRequest.operator();
        final Integer closeParenthesisNumber = saveConditionRequest.closeParenthesisNumber();
        final String firstValue = saveConditionRequest.firstValue();
        final String secondValue = saveConditionRequest.secondValue();
        final String fieldName = advancedFilter.getFieldType().fromFieldName(saveConditionRequest.fieldName()).get().name();


        return new Condition(conditionId, fieldName, firstValue, secondValue, openParenthesisNumber, closeParenthesisNumber, logicalOperatorBefore, filterOperator, conditionGroup);
    }
}
