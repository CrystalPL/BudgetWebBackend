package pl.crystalek.budgetweb.filter;

import pl.crystalek.budgetweb.filter.data.type.FilterDataType;

public interface AdvancedFilterField {

    FilterDataType getFilterDataType();

    String getFieldName();

    default boolean isValueValid(final String value) {
        return getFilterDataType().getValidator().isValueValid(value);
    }
}
