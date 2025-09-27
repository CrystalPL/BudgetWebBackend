package pl.crystalek.budgetweb.receipt.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.ConvertGroup;

import java.util.Set;

public record SaveReceiptRequest(
        @Valid
        @NotNull(message = "INVALID_REQUEST")
        @ConvertGroup(to = SaveReceiptDetails.Validation.class)
        SaveReceiptDetails receiptDetails,

        @NotEmpty(message = "EMPTY_ITEMS")
        Set<
                @Valid
                @NotNull(message = "INVALID_REQUEST")
                @ConvertGroup(to = SaveReceiptItemsData.Validation.class)
                        SaveReceiptItemsData
                > itemsDataList
) {}