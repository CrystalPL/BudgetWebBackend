package pl.crystalek.budgetweb.validation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/validation")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ValidationController {
    ValidationService validationService;

    @GetMapping
    public Map<ValidationEntityType, Validator> getValidator(
            @RequestParam final List<ValidationEntityType> validationEntityTypes
    ) {
        return validationService.getValidators(validationEntityTypes);
    }
}
