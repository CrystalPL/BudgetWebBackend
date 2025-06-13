package pl.crystalek.budgetweb.household;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.household.request.ChangeHouseholdNameRequest;
import pl.crystalek.budgetweb.household.request.CreateHouseholdRequest;
import pl.crystalek.budgetweb.household.request.TransferOwnerRequest;
import pl.crystalek.budgetweb.household.response.ChangeHouseholdNameResponseMessage;
import pl.crystalek.budgetweb.household.response.CreateHouseholdResponseMessage;
import pl.crystalek.budgetweb.household.response.DeleteHouseholdResponseMessage;
import pl.crystalek.budgetweb.household.response.TransferOwnerResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

@RestController
@RequestMapping("/household")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class HouseHoldController {
    HouseholdService householdService;

    @PostMapping("/create")
    public ResponseEntity<ResponseAPI<CreateHouseholdResponseMessage>> create(
            @Validated(CreateHouseholdRequest.Validation.class) @RequestBody final CreateHouseholdRequest createHouseholdRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<CreateHouseholdResponseMessage> response = householdService.create(createHouseholdRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).HOUSEHOLD_CHANGE_NAME)")
    @PostMapping("/changeName")
    public ResponseEntity<ResponseAPI<ChangeHouseholdNameResponseMessage>> changeHouseholdName(
            @Validated(ChangeHouseholdNameRequest.HouseholdNameRequestValidation.class) @RequestBody final ChangeHouseholdNameRequest changeHouseholdNameRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<ChangeHouseholdNameResponseMessage> response = householdService.changeHouseholdName(changeHouseholdNameRequest.householdName(), userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).HOUSEHOLD_DELETE)")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseAPI<DeleteHouseholdResponseMessage>> deleteHousehold(@AuthenticationPrincipal final long userId) {
        final ResponseAPI<DeleteHouseholdResponseMessage> response = householdService.deleteHousehold(userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/transferOwner")
    public ResponseEntity<ResponseAPI<TransferOwnerResponseMessage>> transferHouseholdOwner(
            @Validated @RequestBody final TransferOwnerRequest transferOwnerRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<TransferOwnerResponseMessage> response = householdService.transferOwner(transferOwnerRequest.getMemberId(), userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
