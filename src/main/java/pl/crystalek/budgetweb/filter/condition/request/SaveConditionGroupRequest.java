package pl.crystalek.budgetweb.filter.condition.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.ConvertGroup;
import org.jetbrains.annotations.Nullable;
import pl.crystalek.budgetweb.filter.condition.FilterLogicalOperator;

import java.util.List;

public record SaveConditionGroupRequest(
        @Nullable
        @Positive(message = "INVALID_CONDITION_GROUP_ID", groups = ValidationGroups.InvalidConditionGroupIdNumber.class)
        Long conditionGroupId,

        FilterLogicalOperator logicalOperatorBefore,

        @NotEmpty(message = "EMPTY_CONDITIONS")
        List<
                @Valid
                @NotNull(message = "INVALID_REQUEST")
                @ConvertGroup(to = SaveConditionRequest.Validation.class)
                        SaveConditionRequest
                > conditions
) {

    @GroupSequence({
            ValidationGroups.InvalidConditionGroupIdNumber.class
    })
    public interface Validation {
    }

    interface ValidationGroups {
        interface InvalidConditionGroupIdNumber {
        }
    }
}
