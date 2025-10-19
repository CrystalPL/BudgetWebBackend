package pl.crystalek.budgetweb.filter.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FilterIdRequest(
        @NotNull(message = "MISSING_ID", groups = ValidationGroups.MissingId.class)
        @Positive(message = "INVALID_ID", groups = ValidationGroups.InvalidId.class)
        Long filterId
) {
    @GroupSequence({
            ValidationGroups.MissingId.class,
            ValidationGroups.InvalidId.class,
    })
    public interface Validation {
    }

    interface ValidationGroups {
        interface MissingId {
        }

        interface InvalidId {
        }
    }
}
