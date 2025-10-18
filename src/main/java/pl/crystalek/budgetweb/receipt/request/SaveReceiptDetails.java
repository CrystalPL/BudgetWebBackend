package pl.crystalek.budgetweb.receipt.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import pl.crystalek.budgetweb.receipt.ReceiptConstraints;

import java.time.Instant;

public record SaveReceiptDetails(
        @NotNull(message = "MISSING_RECEIPT_ID", groups = ValidationGroups.MissingReceiptId.class)
        @PositiveOrZero(message = "INVALID_CATEGORY_ID", groups = ValidationGroups.InvalidCategoryId.class)
        Long receiptId,

        @NotBlank(message = "MISSING_NAME", groups = ValidationGroups.NameNotBlank.class)
        @Size(min = ReceiptConstraints.SHOP_NAME_MIN_LENGTH, message = "NAME_TOO_SHORT", groups = ValidationGroups.NameMinSize.class)
        @Size(max = ReceiptConstraints.SHOP_NAME_MAX_LENGTH, message = "NAME_TOO_LONG", groups = ValidationGroups.NameMaxSize.class)
        String shopName,

        @NotNull(message = "MISSING_WHO_PAID_ID", groups = ValidationGroups.MissingWhoPaidId.class)
        @Positive(message = "INVALID_WHO_PAID_ID", groups = ValidationGroups.InvalidWhoPaidId.class)
        Long whoPaidId,

        @NotNull(message = "MISSING_DATE", groups = ValidationGroups.MissingDate.class)
        Instant date,

        @NotNull(message = "MISSING_SETTLED", groups = ValidationGroups.MissingSettled.class)
        Boolean isSettled
) {

    @GroupSequence({
            ValidationGroups.MissingReceiptId.class,
            ValidationGroups.InvalidCategoryId.class,
            ValidationGroups.NameNotBlank.class,
            ValidationGroups.NameMinSize.class,
            ValidationGroups.NameMaxSize.class,
            ValidationGroups.MissingWhoPaidId.class,
            ValidationGroups.InvalidWhoPaidId.class,
            ValidationGroups.MissingDate.class,
            ValidationGroups.MissingSettled.class
    })
    public interface Validation {}

    interface ValidationGroups {
        interface NameNotBlank {}

        interface NameMinSize {}

        interface NameMaxSize {}

        interface MissingReceiptId {}

        interface MissingWhoPaidId {}

        interface InvalidCategoryId {}

        interface InvalidWhoPaidId {}

        interface MissingDate {}

        interface MissingSettled {}
    }
}
