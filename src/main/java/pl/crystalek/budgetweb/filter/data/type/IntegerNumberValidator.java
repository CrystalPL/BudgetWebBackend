package pl.crystalek.budgetweb.filter.data.type;

import pl.crystalek.budgetweb.utils.NumberUtil;

class IntegerNumberValidator implements FilterDataTypeValidator {

    @Override
    public boolean isValueValid(final String value) {
        return NumberUtil.getInt(value).isPresent();
    }
}
