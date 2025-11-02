package pl.crystalek.budgetweb.filter.condition;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.filter.condition.request.SaveFilterConditionRequest;
import pl.crystalek.budgetweb.filter.condition.response.ConditionGroupResponse;
import pl.crystalek.budgetweb.household.constraints.RequireHousehold;

import java.util.List;

@RequireHousehold
@RestController
@RequestMapping("/filter/condition")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ConditionController {
    ConditionFacade conditionFacade;

    @PostMapping("/saveFilterConditions")
    public void saveFilterConditions(
            @Validated(SaveFilterConditionRequest.Validation.class) @RequestBody final SaveFilterConditionRequest saveFilterConditionRequest,
            @AuthenticationPrincipal final long userId
    ) {
        conditionFacade.saveCondition(saveFilterConditionRequest, userId);
    }

    @GetMapping("/{advancedFilterId}")
    public List<ConditionGroupResponse> getConditionGroups(
            @PathVariable final Long advancedFilterId,
            @AuthenticationPrincipal final long userId
    ) {
        return conditionFacade.getConditionGroupResponse(advancedFilterId, userId);
    }
}
