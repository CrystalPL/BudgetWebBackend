package pl.crystalek.budgetweb.filter.condition.validator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.exception.BudgetAppException;
import pl.crystalek.budgetweb.filter.AdvancedFilter;
import pl.crystalek.budgetweb.filter.AdvancedFilterEntityType;
import pl.crystalek.budgetweb.filter.AdvancedFilterField;
import pl.crystalek.budgetweb.filter.condition.FilterOperator;
import pl.crystalek.budgetweb.filter.condition.request.SaveConditionRequest;
import pl.crystalek.budgetweb.filter.condition.response.SaveFilterConditionResponse;
import pl.crystalek.budgetweb.filter.data.type.FilterDataType;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class ConditionValidator implements SaveConditionValidator {
    final List<SaveConditionRequest> saveConditionRequests;
    final AdvancedFilter advancedFilter;
    AdvancedFilterField advancedFilterField;
    SaveConditionRequest saveConditionRequest;
    int openParenthesisNumber;
    int closeParenthesisNumber;

    @Override
    public void validate() {
        isFirstConditionHasLogicalOperatorBefore();

        if (saveConditionRequests.size() <= 1) {
            return;
        }

        for (int i = 0; i < saveConditionRequests.size(); i++) {
            this.saveConditionRequest = saveConditionRequests.get(i);
            isOtherConditionsHasLogicalOperatorBefore(i);
            setAdvancedFilterFieldIfExists();
            incrementParenthesisNumber();
            validateFirstValue();
            validateSecondValue();
            validateParenthesisNumber();
            validateFilterOperatorIsAvailableForField();
        }

        validateOpenCloseParenthesisNumber();
    }

    private void isFirstConditionHasLogicalOperatorBefore() {
        if (saveConditionRequests.getFirst().logicalOperatorBefore() != null) {
            throw new BudgetAppException(SaveFilterConditionResponse.FIRST_CONDITION_CAN_NOT_HAVE_OPERATOR);
        }
    }

    private void isOtherConditionsHasLogicalOperatorBefore(int index) {
        if (index == 0) {
            return;
        }

        if (saveConditionRequest.logicalOperatorBefore() == null) {
            throw new BudgetAppException(SaveFilterConditionResponse.CONDITION_NOT_HAVE_OPERATOR_BEFORE);
        }
    }

    private void setAdvancedFilterFieldIfExists() {
        final AdvancedFilterEntityType fieldType = advancedFilter.getFieldType();
        final Optional<AdvancedFilterField> advancedFilterFieldOptional = fieldType.parseField(saveConditionRequest.fieldName());
        if (advancedFilterFieldOptional.isEmpty()) {
            throw new BudgetAppException(SaveFilterConditionResponse.INVALID_FIELD_NAME);
        }

        advancedFilterField = advancedFilterFieldOptional.get();
    }

    private void incrementParenthesisNumber() {
        openParenthesisNumber += saveConditionRequest.openParenthesisNumber();
        closeParenthesisNumber += saveConditionRequest.closeParenthesisNumber();
    }

    private void validateFirstValue() {
        final String firstValueAsString = saveConditionRequest.firstValue();
        if (!advancedFilterField.isValueValid(firstValueAsString)) {
            throw new BudgetAppException(SaveFilterConditionResponse.INVALID_FIRST_VALUE);
        }
    }

    private void validateSecondValue() {
        final String secondValueAsString = saveConditionRequest.secondValue();
        final boolean isSecondValueValid = advancedFilterField.isValueValid(secondValueAsString);
        if (saveConditionRequest.operator() == FilterOperator.BETWEEN) {
            if (!isSecondValueValid) {
                throw new BudgetAppException(SaveFilterConditionResponse.INVALID_SECOND_VALUE);
            }
        } else {
            if (isSecondValueValid) {
                throw new BudgetAppException(SaveFilterConditionResponse.OPERATOR_NOT_BETWEEN_WHEN_SECOND_VALUE_SET);
            }
        }
    }

    //TODO KIEDYŚ WARTO BY BYŁO BARDZIEJ SIE ZASTANOWIĆ JAK BARDZIEJ WALIDOWAĆ TE NAWIASY
    private void validateParenthesisNumber() {
        final Integer openParenthesisNumber = saveConditionRequest.openParenthesisNumber();
        final Integer closeParenthesisNumber = saveConditionRequest.closeParenthesisNumber();
        final long matchingParenthesisCount = Math.min(openParenthesisNumber, closeParenthesisNumber);
        if (matchingParenthesisCount != 0) {
            throw new BudgetAppException(SaveFilterConditionResponse.UNNECESSARY_PARENTHESES);
        }
    }

    private void validateFilterOperatorIsAvailableForField() {
        final FilterDataType filterDataType = advancedFilterField.getFilterDataType();
        final boolean operatorAvailable = filterDataType.isOperatorAvailable(saveConditionRequest.operator());
        if (!operatorAvailable) {
            throw new BudgetAppException(SaveFilterConditionResponse.OPERATOR_NOT_AVAILABLE_FOR_FIELD);
        }
    }

    private void validateOpenCloseParenthesisNumber() {
        if (openParenthesisNumber != closeParenthesisNumber) {
            throw new BudgetAppException(SaveFilterConditionResponse.MISMATCH_OPEN_CLOSED_PARENTHESIS_NUMBER);
        }
    }
}
