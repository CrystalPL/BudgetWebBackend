package pl.crystalek.budgetweb.filter.data.type;

import org.apache.commons.lang3.StringUtils;

class TextValidator implements FilterDataTypeValidator {

    @Override
    public boolean isValueValid(final String value) {
        return StringUtils.isNotBlank(value);
    }
}
