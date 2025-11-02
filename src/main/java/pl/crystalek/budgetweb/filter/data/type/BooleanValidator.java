package pl.crystalek.budgetweb.filter.data.type;

class BooleanValidator implements FilterDataTypeValidator {

    @Override
    public boolean isValueValid(final String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }
}
