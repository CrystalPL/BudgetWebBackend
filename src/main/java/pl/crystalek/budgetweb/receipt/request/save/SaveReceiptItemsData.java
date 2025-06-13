package pl.crystalek.budgetweb.receipt.request.save;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.Nullable;

public record SaveReceiptItemsData(
        @NotNull(message = "MISSING_RECEIPT_ITEM_ID", groups = ValidationGroups.MissingReceiptId.class)
        @PositiveOrZero(message = "INVALID_RECEIPT_ITEM_ID", groups = ValidationGroups.InvalidReceiptItemId.class)
        Long receiptItemId,

        @NotBlank(message = "MISSING_NAME", groups = ValidationGroups.NameNotBlank.class)
        @Size(min = 2, message = "NAME_TOO_SHORT", groups = ValidationGroups.NameMinSize.class)
        @Size(max = 64, message = "NAME_TOO_LONG", groups = ValidationGroups.NameMaxSize.class)
        String productName,

        @NotNull(message = "MISSING_QUANTITY", groups = ValidationGroups.MissingQuantity.class)
        @Positive(message = "INVALID_QUANTITY", groups = ValidationGroups.InvalidQuantity.class)
        Double quantity,

        @NotNull(message = "MISSING_PRICE", groups = ValidationGroups.MissingPrice.class)
        @Positive(message = "INVALID_PRICE", groups = ValidationGroups.InvalidPrice.class)
        Double price,

        @NotNull(message = "MISSING_CATEGORY_ID", groups = ValidationGroups.MissingCategoryId.class)
        @Positive(message = "INVALID_CATEGORY_ID", groups = ValidationGroups.InvalidCategoryId.class)
        Long categoryId,

        @Nullable
        Double moneyDividing,

        @Nullable
        Long userToReturnMoneyId
) {

    @GroupSequence({
            SaveReceiptItemsData.ValidationGroups.MissingReceiptId.class,
            SaveReceiptItemsData.ValidationGroups.InvalidReceiptItemId.class,
            SaveReceiptItemsData.ValidationGroups.NameNotBlank.class,
            SaveReceiptItemsData.ValidationGroups.NameMinSize.class,
            SaveReceiptItemsData.ValidationGroups.NameMaxSize.class,
            SaveReceiptItemsData.ValidationGroups.MissingQuantity.class,
            SaveReceiptItemsData.ValidationGroups.InvalidQuantity.class,
            SaveReceiptItemsData.ValidationGroups.MissingPrice.class,
            SaveReceiptItemsData.ValidationGroups.InvalidPrice.class,
            SaveReceiptItemsData.ValidationGroups.MissingCategoryId.class,
            SaveReceiptItemsData.ValidationGroups.InvalidNumberFormat.class,
            SaveReceiptItemsData.ValidationGroups.InvalidCategoryId.class
    })
    public interface Validation {}

    interface ValidationGroups {
        interface MissingReceiptId {}

        interface InvalidNumberFormat {}

        interface NameNotBlank {}

        interface NameMinSize {}

        interface NameMaxSize {}

        interface MissingCategoryId {}

        interface MissingQuantity {}

        interface MissingPrice {}

        interface InvalidQuantity {}

        interface InvalidReceiptItemId {}

        interface InvalidPrice {}

        interface InvalidCategoryId {}
    }
}
