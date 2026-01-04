package pl.crystalek.budgetweb.filter.condition.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import pl.crystalek.budgetweb.filter.condition.model.FilterLogicalOperator;
import pl.crystalek.budgetweb.filter.condition.model.FilterOperator;

public record SaveConditionRequest(
        @Positive(message = "INVALID_CONDITION_ID", groups = ValidationGroups.InvalidConditionId.class)
        Long conditionId,

        @NotNull(message = "MISSING_FIELD_NAME", groups = ValidationGroups.MissingFieldName.class)
        String fieldName,

        @NotNull(message = "MISSING_FIRST_VALUE", groups = ValidationGroups.MissingFirstValue.class)
        String firstValue,

        String secondValue,

        @PositiveOrZero(message = "INVALID_OPEN_PARENTHESIS_NUMBER", groups = ValidationGroups.InvalidOpenParenthesisNumber.class)
        Integer openParenthesisNumber,

        @PositiveOrZero(message = "INVALID_CLOSE_PARENTHESIS_NUMBER", groups = ValidationGroups.InvalidCloseParenthesisNumber.class)
        Integer closeParenthesisNumber,

        FilterLogicalOperator logicalOperatorBefore,

        @NotNull(message = "MISSING_FILTER_OPERATOR", groups = ValidationGroups.MissingFilterOperator.class)
        FilterOperator operator
) {

    public Integer openParenthesisNumber() {
        return openParenthesisNumber != null ? openParenthesisNumber : 0;
    }

    public Integer closeParenthesisNumber() {
        return closeParenthesisNumber != null ? closeParenthesisNumber : 0;
    }

    @GroupSequence({
            ValidationGroups.InvalidConditionId.class,
            ValidationGroups.MissingFieldName.class,
            ValidationGroups.MissingFirstValue.class,
            ValidationGroups.InvalidOpenParenthesisNumber.class,
            ValidationGroups.InvalidCloseParenthesisNumber.class,
            ValidationGroups.MissingFilterOperator.class
    })
    public interface Validation {
    }

    interface ValidationGroups {
        interface InvalidConditionId {
        }

        interface MissingFieldName {
        }

        interface MissingFirstValue {
        }

        interface InvalidOpenParenthesisNumber {
        }

        interface InvalidCloseParenthesisNumber {
        }

        interface MissingFilterOperator {
        }
    }
}
