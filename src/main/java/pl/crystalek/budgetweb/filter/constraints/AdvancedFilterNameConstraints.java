package pl.crystalek.budgetweb.filter.constraints;

import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

public class AdvancedFilterNameConstraints implements Validator {

    public static final int ADVANCED_FILTER_NAME_MIN_LENGTH = 3;
    public static final int ADVANCED_FILTER_NAME_MAX_LENGTH = 32;
    public static final int ADVANCED_FILTER_DESCRIPTION_MAX_LENGTH = 10024;

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.ADVANCED_FILTER;
    }
}
