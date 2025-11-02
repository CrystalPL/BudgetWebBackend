package pl.crystalek.budgetweb.receipt.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.filter.AdvancedFilterField;
import pl.crystalek.budgetweb.filter.data.type.FilterDataType;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ReceiptFilterField implements AdvancedFilterField {
    SHOPPING_TIME("shoppingTime", FilterDataType.DATE),
    SHOP_NAME("shop", FilterDataType.TEXT),
    WHO_PAID("whoPaid", FilterDataType.AUTOCOMPLETE),
    SETTLED("settled", FilterDataType.BOOLEAN),
    AMOUNT("amount", FilterDataType.DECIMAL_NUMBER),
    CREATION_TIME("creationTime", FilterDataType.DATE);

    String fieldName;
    FilterDataType filterDataType;
}
