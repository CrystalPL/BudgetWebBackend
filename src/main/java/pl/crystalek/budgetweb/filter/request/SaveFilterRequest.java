package pl.crystalek.budgetweb.filter.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pl.crystalek.budgetweb.filter.AdvancedFilterEntityType;
import pl.crystalek.budgetweb.filter.constraints.AdvancedFilterNameConstraints;

public record SaveFilterRequest(
        @Positive
        Long id,

        @NotBlank(message = "MISSING_NAME", groups = ValidationGroups.NameNotBlank.class)
        @Size(min = AdvancedFilterNameConstraints.ADVANCED_FILTER_NAME_MIN_LENGTH, message = "NAME_TOO_SHORT", groups = ValidationGroups.NameMinSize.class)
        @Size(max = AdvancedFilterNameConstraints.ADVANCED_FILTER_NAME_MAX_LENGTH, message = "NAME_TOO_LONG", groups = ValidationGroups.NameMaxSize.class)
        String name,

        @Size(max = AdvancedFilterNameConstraints.ADVANCED_FILTER_DESCRIPTION_MAX_LENGTH, message = "DESCRIPTION_TOO_LONG", groups = ValidationGroups.DescriptionMaxSize.class)
        String description,

        @NotNull(message = "MISSING_FILTER_ENTITY_TYPE", groups = ValidationGroups.MissingFilterEntityType.class)
        AdvancedFilterEntityType advancedFilterEntityType
) {
    @GroupSequence({
            ValidationGroups.NameNotBlank.class,
            ValidationGroups.NameMinSize.class,
            ValidationGroups.NameMaxSize.class,
            ValidationGroups.DescriptionMaxSize.class,
            ValidationGroups.MissingFilterEntityType.class
    })
    public interface Validation {
    }

    interface ValidationGroups {
        interface NameNotBlank {
        }

        interface NameMinSize {
        }

        interface NameMaxSize {
        }

        interface DescriptionMaxSize {
        }

        interface MissingFilterEntityType {
        }
    }
}
