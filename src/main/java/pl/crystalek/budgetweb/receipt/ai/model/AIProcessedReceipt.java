package pl.crystalek.budgetweb.receipt.ai.model;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

public record AIProcessedReceipt(@Nullable String shop, @Nullable Instant shoppingTime,
                                 List<AIProcessedReceiptItem> aiReceiptItems) {
}
