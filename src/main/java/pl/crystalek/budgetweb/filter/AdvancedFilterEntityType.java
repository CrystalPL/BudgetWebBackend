package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.receipt.ReceiptFilterField;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum AdvancedFilterEntityType {
    RECEIPT(ReceiptFilterField.class);

    Class<? extends Enum<? extends AdvancedFilterField>> filterFieldClass;

    public Optional<AdvancedFilterField> fromFieldName(String fieldName) {
        return Arrays.stream(filterFieldClass.getEnumConstants())
                .map(AdvancedFilterField.class::cast)
                .filter(field -> field.getFieldName().equals(fieldName))
                .findFirst();
    }

    public Optional<AdvancedFilterField> fromEnumName(String fieldEnumName) {
        try {
            return Optional.of(Enum.valueOf(filterFieldClass.asSubclass(Enum.class), fieldEnumName)).map(AdvancedFilterField.class::cast);
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
