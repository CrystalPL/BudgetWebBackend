package pl.crystalek.budgetweb.filter;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.filter.condition.ConditionFacade;
import pl.crystalek.budgetweb.filter.condition.request.SaveFilterConditionRequest;
import pl.crystalek.budgetweb.filter.condition.response.ConditionGroupResponse;
import pl.crystalek.budgetweb.filter.request.DuplicateFilterRequest;
import pl.crystalek.budgetweb.filter.request.FilterIdRequest;
import pl.crystalek.budgetweb.filter.request.SaveFilterRequest;
import pl.crystalek.budgetweb.filter.response.AdvancedFilterListGetterResponse;
import pl.crystalek.budgetweb.filter.response.BaseFilterResponseMessage;
import pl.crystalek.budgetweb.household.constraints.RequireHousehold;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.List;

@RequireHousehold
@RestController
@RequestMapping("/filter")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class AdvancedFilterController {
    AdvancedFilterFacade advancedFilterFacade;
    ConditionFacade conditionFacade;

    @PostMapping("/condition/save")
    public void saveFilterConditions(
            @Validated(SaveFilterConditionRequest.Validation.class) @RequestBody final SaveFilterConditionRequest saveFilterConditionRequest,
            @AuthenticationPrincipal final long userId
    ) {
        conditionFacade.saveCondition(saveFilterConditionRequest, userId);
    }

    @GetMapping("/condition/{advancedFilterId}")
    public List<ConditionGroupResponse> getConditionGroups(
            @PathVariable final Long advancedFilterId,
            @AuthenticationPrincipal final long userId
    ) {
        return conditionFacade.getConditionGroupResponse(advancedFilterId, userId);
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseAPI<BaseFilterResponseMessage>> saveFilter(
            @Validated(SaveFilterRequest.Validation.class) @RequestBody final SaveFilterRequest saveFilterRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<BaseFilterResponseMessage> response = advancedFilterFacade.saveFilter(saveFilterRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/activate")
    public ResponseEntity<ResponseAPI<BaseFilterResponseMessage>> activateFilter(
            @Validated(FilterIdRequest.Validation.class) @RequestBody final FilterIdRequest saveFilterRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<BaseFilterResponseMessage> response = advancedFilterFacade.activateFilter(saveFilterRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseAPI<BaseFilterResponseMessage>> deleteFilter(
            @PathVariable @NotNull final Long id,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<BaseFilterResponseMessage> response = advancedFilterFacade.deleteFilter(id, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/duplicate")
    public ResponseEntity<ResponseAPI<BaseFilterResponseMessage>> duplicateFilter(
            @Validated(DuplicateFilterRequest.Validation.class) @RequestBody final DuplicateFilterRequest duplicateFilterRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<BaseFilterResponseMessage> response = advancedFilterFacade.duplicateFilter(duplicateFilterRequest, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{filterEntityType}")
    public List<AdvancedFilterListGetterResponse> getFilters(
            @PathVariable @NotNull final AdvancedFilterEntityType filterEntityType,
            @AuthenticationPrincipal final long userId
    ) {
        return advancedFilterFacade.getAdvancedFilterList(filterEntityType, userId);
    }
}
