package pl.crystalek.budgetweb.filter.condition.validator;

import pl.crystalek.budgetweb.exception.BudgetAppException;
import pl.crystalek.budgetweb.filter.AdvancedFilter;
import pl.crystalek.budgetweb.filter.condition.request.SaveConditionGroupRequest;

import java.util.ArrayList;
import java.util.List;

public interface SaveConditionValidator {

    static List<SaveConditionValidator> getValidators(final List<SaveConditionGroupRequest> saveConditionGroupRequests, final AdvancedFilter advancedFilter) {
        final List<SaveConditionValidator> validators = new ArrayList<>(2);

        validators.add(new ConditionGroupValidator(saveConditionGroupRequests));
        validators.addAll(getConditionValidators(saveConditionGroupRequests, advancedFilter));

        return validators;
    }

    private static List<SaveConditionValidator> getConditionValidators(final List<SaveConditionGroupRequest> saveConditionGroupRequests, final AdvancedFilter advancedFilter) {
        return saveConditionGroupRequests.stream()
                .map(SaveConditionGroupRequest::conditions)
                .map(conditionRequests -> (SaveConditionValidator) new ConditionValidator(conditionRequests, advancedFilter))
                .toList();
    }

    void validate() throws BudgetAppException;
}