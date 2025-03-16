package pl.crystalek.budgetweb.household;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.model.ChangeHouseholdNameRequest;
import pl.crystalek.budgetweb.household.model.ChangeHouseholdNameResponseMessage;
import pl.crystalek.budgetweb.household.model.CreateHouseholdRequest;
import pl.crystalek.budgetweb.household.model.CreateHouseholdResponseMessage;
import pl.crystalek.budgetweb.household.model.DeleteHouseholdResponseMessage;
import pl.crystalek.budgetweb.household.model.TransferOwnerRequest;
import pl.crystalek.budgetweb.household.model.TransferOwnerResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseHoldController {
    HouseholdService householdService;

    @PostMapping("/create")
    private ResponseEntity<ResponseAPI<CreateHouseholdResponseMessage>> create(@Validated(CreateHouseholdRequest.HouseholdNameRequestValidation.class) @RequestBody final CreateHouseholdRequest createHouseholdRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<CreateHouseholdResponseMessage> response = householdService.create(createHouseholdRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/changeName")
    private ResponseEntity<ResponseAPI<ChangeHouseholdNameResponseMessage>> changeHouseholdName(@Validated(ChangeHouseholdNameRequest.HouseholdNameRequestValidation.class) @RequestBody final ChangeHouseholdNameRequest changeHouseholdNameRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<ChangeHouseholdNameResponseMessage> response = householdService.changeHouseholdName(changeHouseholdNameRequest.householdName(), userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete")
    private ResponseEntity<ResponseAPI<DeleteHouseholdResponseMessage>> deleteHousehold() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<DeleteHouseholdResponseMessage> response = householdService.deleteHousehold(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/transferOwner")
    private ResponseEntity<ResponseAPI<TransferOwnerResponseMessage>> transferHouseholdOwner(@Validated @RequestBody final TransferOwnerRequest transferOwnerRequest) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<TransferOwnerResponseMessage> response = householdService.transferOwner(transferOwnerRequest.getMemberId(), userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
