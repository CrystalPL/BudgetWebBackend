package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    )),
    NUMBER(EnumSet.of(
            FilterOperator.EQUALS,
            FilterOperator.NOT_EQUALS,
            FilterOperator.GREATER_THAN,
            FilterOperator.LESS_THAN,
            FilterOperator.GREATER_THAN_OR_EQUAL,
            FilterOperator.LESS_THAN_OR_EQUAL,
            FilterOperator.BETWEEN
    )),
    DATE(EnumSet.of(
            FilterOperator.EQUALS,
            FilterOperator.NOT_EQUALS,
            FilterOperator.BEFORE,
            FilterOperator.AFTER,
            FilterOperator.BETWEEN
    )),
    BOOLEAN(EnumSet.of(
            FilterOperator.EQUALS
    )),
    AUTOCOMPLETE(EnumSet.of(
            FilterOperator.NOT_EQUALS,
            FilterOperator.EQUALS
    ));

    EnumSet<FilterOperator> availableOperators;
}
