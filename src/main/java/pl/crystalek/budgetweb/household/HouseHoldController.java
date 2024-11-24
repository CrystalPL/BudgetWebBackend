package pl.crystalek.budgetweb.household;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.model.CreateHouseholdRequest;
import pl.crystalek.budgetweb.household.model.CreateHouseholdResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseHoldController {
    HouseholdService householdService;

    @PostMapping("/create")
    private ResponseEntity<ResponseAPI<CreateHouseholdResponseMessage>> create(@Validated(CreateHouseholdRequest.CreateHouseholdRequestValidation.class) @RequestBody final CreateHouseholdRequest createHouseholdRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<CreateHouseholdResponseMessage> response = householdService.create(createHouseholdRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}
