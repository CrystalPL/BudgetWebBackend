package pl.crystalek.budgetweb.receipt;

import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
public class ReceiptConstraints implements Validator {

    public static final int SHOP_NAME_MIN_LENGTH = 3;
    public static final int SHOP_NAME_MAX_LENGTH = 64;

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.RECEIPT;
    }
}
