package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.receipt.filter.ReceiptFilterField;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum AdvancedFilterEntityType {
    RECEIPT(ReceiptFilterField.class);

    Class<? extends Enum<? extends AdvancedFilterField>> filterFieldClass;

    public Optional<AdvancedFilterField> parseField(String fieldEnumName) {
        try {
            final AdvancedFilterField advancedFilterField = Enum.valueOf(filterFieldClass.asSubclass(Enum.class), fieldEnumName);
            return Optional.of(advancedFilterField);
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
