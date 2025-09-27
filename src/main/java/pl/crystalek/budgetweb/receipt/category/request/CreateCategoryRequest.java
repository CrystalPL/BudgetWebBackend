package pl.crystalek.budgetweb.receipt.category.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "MISSING_NAME", groups = CreateCategoryRequest.ValidationGroups.NameNotBlank.class)
        @Size(min = 2, message = "NAME_TOO_SHORT", groups = CreateCategoryRequest.ValidationGroups.NameMinSize.class)
        @Size(max = 32, message = "NAME_TOO_LONG", groups = CreateCategoryRequest.ValidationGroups.NameMaxSize.class)
        String name,

        @NotBlank(message = "MISSING_COLOR", groups = CreateCategoryRequest.ValidationGroups.MissingColor.class)
        @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "INVALID_COLOR_FORMAT", groups = CreateCategoryRequest.ValidationGroups.InvalidColorFormat.class)
        String color
) {
    @GroupSequence({CreateCategoryRequest.ValidationGroups.NameNotBlank.class, CreateCategoryRequest.ValidationGroups.NameMinSize.class,
            CreateCategoryRequest.ValidationGroups.NameMaxSize.class, CreateCategoryRequest.ValidationGroups.MissingColor.class,
            CreateCategoryRequest.ValidationGroups.InvalidColorFormat.class})
    public interface CreateCategoryRequestValidation {}

    interface ValidationGroups {
        interface NameNotBlank {}

        interface NameMinSize {}

        interface NameMaxSize {}

        interface MissingColor {}

        interface InvalidColorFormat {}
    }
}
