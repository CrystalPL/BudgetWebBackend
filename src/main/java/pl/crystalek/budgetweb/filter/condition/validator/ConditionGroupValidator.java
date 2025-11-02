package pl.crystalek.budgetweb.filter.condition.validator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.exception.BudgetAppException;
import pl.crystalek.budgetweb.filter.condition.request.SaveConditionGroupRequest;
import pl.crystalek.budgetweb.filter.condition.response.SaveFilterConditionResponse;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ConditionGroupValidator implements SaveConditionValidator {
    List<SaveConditionGroupRequest> saveConditionGroupRequests;

    @Override
    public void validate() {
        isFirstGroupHasLogicalOperatorBefore();
        isOtherGroupsHasLogicalOperatorBefore();
    }

    private void isFirstGroupHasLogicalOperatorBefore() {
        final SaveConditionGroupRequest firstConditionGroup = saveConditionGroupRequests.getFirst();
        if (firstConditionGroup.logicalOperatorBefore() != null) {
            throw new BudgetAppException(SaveFilterConditionResponse.FIRST_GROUP_CAN_NOT_HAVE_OPERATOR);
        }
    }

    private void isOtherGroupsHasLogicalOperatorBefore() {
        if (saveConditionGroupRequests.size() <= 1) {
            return;
        }

        for (int i = 1; i < saveConditionGroupRequests.size(); i++) {
            final SaveConditionGroupRequest conditionGroupRequest = saveConditionGroupRequests.get(i);
            if (conditionGroupRequest.logicalOperatorBefore() == null) {
                throw new BudgetAppException(SaveFilterConditionResponse.GROUP_NOT_HAVE_OPERATOR_BEFORE);
            }

//            final List<SaveConditionRequest> conditions = conditionGroupRequest.conditions();
//            new ConditionValidator(conditions, advancedFilter).val
        }
    }
}
