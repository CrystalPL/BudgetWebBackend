package pl.crystalek.budgetweb.filter.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pl.crystalek.budgetweb.filter.constraints.AdvancedFilterNameConstraints;

public record DuplicateFilterRequest(

        @NotNull(message = "MISSING_ID")
        @Positive(message = "INVALID_ID")
        Long id,

        @NotBlank(message = "MISSING_NAME", groups = ValidationGroups.NameNotBlank.class)
        @Size(min = AdvancedFilterNameConstraints.ADVANCED_FILTER_NAME_MIN_LENGTH, message = "NAME_TOO_SHORT", groups = ValidationGroups.NameMinSize.class)
        @Size(max = AdvancedFilterNameConstraints.ADVANCED_FILTER_NAME_MAX_LENGTH, message = "NAME_TOO_LONG", groups = ValidationGroups.NameMaxSize.class)
        String name
) {

    @GroupSequence({
            ValidationGroups.MissingId.class,
            ValidationGroups.InvalidId.class,
            ValidationGroups.NameNotBlank.class,
            ValidationGroups.NameMinSize.class,
            ValidationGroups.NameMaxSize.class
    })
    public interface Validation {
    }

    interface ValidationGroups {
        interface MissingId {
        }

        interface InvalidId {
        }

        interface NameNotBlank {
        }

        interface NameMinSize {
        }

        interface NameMaxSize {
        }
    }
}
