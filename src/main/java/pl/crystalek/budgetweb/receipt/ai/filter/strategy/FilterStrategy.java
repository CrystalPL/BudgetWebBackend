package pl.crystalek.budgetweb.receipt.ai.filter.strategy;

import java.util.List;

public interface FilterStrategy {
    static List<FilterStrategy> getAllFilters() {
        return List.of(
                new ExactMatchFilterStrategy(),
                new SingleCharMissingFilterStrategy(),
                new MultiWordFilterStrategy(),
                new DifferentOrderWithMismatchFilterStrategy()
        );
    }

    String apply(final String input, final String phrase);
}
