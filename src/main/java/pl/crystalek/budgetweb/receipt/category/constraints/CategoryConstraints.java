package pl.crystalek.budgetweb.receipt.category.constraints;

import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
public class CategoryConstraints implements Validator {

    public static final int CATEGORY_NAME_MIN_LENGTH = 3;
    public static final int CATEGORY_NAME_MAX_LENGTH = 32;
    public static final String COLOR_FORMAT_REGEX = "^#[A-Fa-f0-9]{6}$";

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.CATEGORY;
    }
}
