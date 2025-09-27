package pl.crystalek.budgetweb.validation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validation/{validationEntityType}")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ValidationController {
    ValidationService validationService;

    @GetMapping
    public Validator getValidator(
            @PathVariable final ValidationEntityType validationEntityType
    ) {
        return validationService.getValidator(validationEntityType);
    }
}
