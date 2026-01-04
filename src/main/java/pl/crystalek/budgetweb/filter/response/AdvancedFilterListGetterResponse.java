package pl.crystalek.budgetweb.filter.response;

import java.time.Instant;

public record AdvancedFilterListGetterResponse(
        Long id,
        String name,
        String description,
        Boolean active,
        Instant createdAt,
        Instant updatedAt,
        Long totalConditions,
        Long totalGroups
) {
}
