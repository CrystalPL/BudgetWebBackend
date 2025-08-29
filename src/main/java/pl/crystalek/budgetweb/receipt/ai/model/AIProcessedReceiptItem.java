package pl.crystalek.budgetweb.receipt.ai.model;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record AIProcessedReceiptItem(@Nullable String productName, @Nullable Double quantity, @Nullable Double price,
                                     @Nullable String category, List<Double> discounts) {
    public Double getSumOfDiscounts() {
        return discounts.stream()
                .mapToDouble(Double::doubleValue)
                .map(Math::abs)
                .sum();
    }
}
