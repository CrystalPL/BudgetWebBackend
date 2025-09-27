package pl.crystalek.budgetweb.receipt.category.request;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditCategoryRequest(
        @NotNull(message = "MISSING_CATEGORY_ID", groups = EditCategoryRequest.ValidationGroups.MissingCategoryId.class)
        @Pattern(regexp = "^[1-9][0-9]*$", message = "ERROR_NUMBER_FORMAT", groups = EditCategoryRequest.ValidationGroups.InvalidNumberFormat.class)
        String categoryId,

        @NotBlank(message = "MISSING_NAME", groups = EditCategoryRequest.ValidationGroups.NameNotBlank.class)
        @Size(min = 2, message = "NAME_TOO_SHORT", groups = EditCategoryRequest.ValidationGroups.NameMinSize.class)
        @Size(max = 32, message = "NAME_TOO_LONG", groups = EditCategoryRequest.ValidationGroups.NameMaxSize.class)
        String name,

        @NotBlank(message = "MISSING_COLOR", groups = EditCategoryRequest.ValidationGroups.MissingColor.class)
        @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "INVALID_COLOR_FORMAT", groups = EditCategoryRequest.ValidationGroups.InvalidColorFormat.class)
        String color
) {
    public Long getCategoryId() {
        return Long.parseLong(categoryId);
    }

    @GroupSequence({EditCategoryRequest.ValidationGroups.NameNotBlank.class, EditCategoryRequest.ValidationGroups.NameMinSize.class,
            EditCategoryRequest.ValidationGroups.NameMaxSize.class, EditCategoryRequest.ValidationGroups.MissingColor.class,
            EditCategoryRequest.ValidationGroups.InvalidColorFormat.class})
    public interface EditCategoryRequestValidation {}

    interface ValidationGroups {
        interface MissingCategoryId {}

        interface InvalidNumberFormat {}

        interface NameNotBlank {}

        interface NameMinSize {}

        interface NameMaxSize {}

        interface MissingColor {}

        interface InvalidColorFormat {}
    }
}
