package pl.crystalek.budgetweb.filter.condition.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.ConvertGroup;

import java.util.List;

public record SaveFilterConditionRequest(
        @NotNull(message = "MISSING_ADVANCED_FILTER_ID", groups = ValidationGroups.MissingAdvancedFilterId.class)
        @Positive(message = "INVALID_ADVANCED_FILTER_ID", groups = ValidationGroups.InvalidAdvancedFilterIdNumber.class)
        Long advancedFilterId,

        @NotEmpty(message = "EMPTY_CONDITION_GROUPS")
        List<
                @Valid
                @NotNull(message = "INVALID_REQUEST")
                @ConvertGroup(to = SaveConditionGroupRequest.Validation.class)
                        SaveConditionGroupRequest
                > conditionGroups
) {

    @GroupSequence({
            ValidationGroups.MissingAdvancedFilterId.class,
            ValidationGroups.InvalidAdvancedFilterIdNumber.class
    })
    public interface Validation {
    }

    interface ValidationGroups {
        interface MissingAdvancedFilterId {
        }

        interface InvalidAdvancedFilterIdNumber {
        }
    }
}
