package pl.crystalek.budgetweb.filter.data.type;

import pl.crystalek.budgetweb.utils.DateParserUtil;

class DateValidator implements FilterDataTypeValidator {

    @Override
    public boolean isValueValid(final String value) {
        return DateParserUtil.parseDate(value).isPresent();
    }
}
