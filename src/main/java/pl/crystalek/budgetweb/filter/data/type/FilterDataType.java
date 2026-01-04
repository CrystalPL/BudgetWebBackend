package pl.crystalek.budgetweb.filter.data.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.filter.condition.model.FilterOperator;

import java.util.EnumSet;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum FilterDataType {
    TEXT(EnumSet.of(
            FilterOperator.CONTAINS,
            FilterOperator.NOT_CONTAINS,
            FilterOperator.EQUALS,
            FilterOperator.NOT_EQUALS,
            FilterOperator.STARTS_WITH,
            FilterOperator.ENDS_WITH
    ), new TextValidator()),
    DECIMAL_NUMBER(EnumSet.of(
            FilterOperator.EQUALS,
            FilterOperator.NOT_EQUALS,
            FilterOperator.GREATER_THAN,
            FilterOperator.LESS_THAN,
            FilterOperator.GREATER_THAN_OR_EQUAL,
            FilterOperator.LESS_THAN_OR_EQUAL,
            FilterOperator.BETWEEN
    ), new DecimalNumberValidator()),
    INTEGER_NUMBER(EnumSet.of(
            FilterOperator.EQUALS,
            FilterOperator.NOT_EQUALS,
            FilterOperator.GREATER_THAN,
            FilterOperator.LESS_THAN,
            FilterOperator.GREATER_THAN_OR_EQUAL,
            FilterOperator.LESS_THAN_OR_EQUAL,
            FilterOperator.BETWEEN
    ), new IntegerNumberValidator()),
    DATE(EnumSet.of(
            FilterOperator.EQUALS,
            FilterOperator.NOT_EQUALS,
            FilterOperator.BEFORE,
            FilterOperator.AFTER,
            FilterOperator.BETWEEN
    ), new DateValidator()),
    BOOLEAN(EnumSet.of(
            FilterOperator.EQUALS
    ), new BooleanValidator()),
    AUTOCOMPLETE(EnumSet.of(
            FilterOperator.NOT_EQUALS,
            FilterOperator.EQUALS
    ), new AutocompleteValidator());

    EnumSet<FilterOperator> availableOperators;
    @Getter
    FilterDataTypeValidator validator;

    public boolean isOperatorAvailable(final FilterOperator filterOperator) {
        return availableOperators.contains(filterOperator);
    }
}
