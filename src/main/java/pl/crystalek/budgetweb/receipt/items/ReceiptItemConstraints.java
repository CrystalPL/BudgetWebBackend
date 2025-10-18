package pl.crystalek.budgetweb.receipt.items;

import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.ValidationEntityType;
import pl.crystalek.budgetweb.validation.Validator;

@Component
public class ReceiptItemConstraints implements Validator {

    public static final int PRODUCT_NAME_MIN_LENGTH = 3;
    public static final int PRODUCT_NAME_MAX_LENGTH = 64;

    @Override
    public ValidationEntityType getEntityType() {
        return ValidationEntityType.RECEIPT_ITEM;
    }
}
