package pl.crystalek.budgetweb.receipt.ai.model;

import pl.crystalek.budgetweb.receipt.items.response.ReceiptItemCategoryDTO;

public record AIReceiptItem(String productName, Double quantity, Double price, ReceiptItemCategoryDTO category) {
}
