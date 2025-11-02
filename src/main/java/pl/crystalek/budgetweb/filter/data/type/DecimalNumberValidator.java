package pl.crystalek.budgetweb.filter.data.type;

import pl.crystalek.budgetweb.utils.NumberUtil;

class DecimalNumberValidator implements FilterDataTypeValidator {
    @Override
    public boolean isValueValid(final String value) {
        return NumberUtil.getDouble(value).isPresent();
    }
}
