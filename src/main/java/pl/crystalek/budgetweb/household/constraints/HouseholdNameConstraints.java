package pl.crystalek.budgetweb.household.constraints;

import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
public class HouseholdNameConstraints implements Validator {
    public static final int HOUSEHOLD_NAME_MIN_LENGTH = 3;
    public static final int HOUSEHOLD_NAME_MAX_LENGTH = 32;

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.HOUSEHOLD;
    }
}
