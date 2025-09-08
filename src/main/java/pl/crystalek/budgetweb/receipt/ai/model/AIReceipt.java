package pl.crystalek.budgetweb.receipt.ai.model;

import java.time.Instant;
import java.util.List;

public record AIReceipt(String shop, Instant shoppingTime, List<AIReceiptItem> items) {
}
