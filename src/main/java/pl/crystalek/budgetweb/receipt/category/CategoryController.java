package pl.crystalek.budgetweb.receipt.category;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crystalek.budgetweb.receipt.category.request.CreateCategoryRequest;
import pl.crystalek.budgetweb.receipt.category.request.EditCategoryRequest;
import pl.crystalek.budgetweb.receipt.category.response.CreateCategoryResponseMessage;
import pl.crystalek.budgetweb.receipt.category.response.DeleteCategoryResponseMessage;
import pl.crystalek.budgetweb.receipt.category.response.EditCategoryResponseMessage;
import pl.crystalek.budgetweb.receipt.category.response.GetCategoryResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class CategoryController {
    CategoryService categoryService;

    @GetMapping
    public Set<GetCategoryResponse> getCategoryResponses(@AuthenticationPrincipal final long userId) {
        return categoryService.getCategories(userId);
    }

    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).CATEGORY_CREATE)")
    @PostMapping("/create")
    public ResponseEntity<ResponseAPI<CreateCategoryResponseMessage>> createCategory(
            @Validated(CreateCategoryRequest.CreateCategoryRequestValidation.class) @RequestBody final CreateCategoryRequest createCategoryRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<CreateCategoryResponseMessage> response = categoryService.createCategory(createCategoryRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/edit")
    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).CATEGORY_EDIT)")
    public ResponseEntity<ResponseAPI<EditCategoryResponseMessage>> editCategory(
            @Validated(EditCategoryRequest.EditCategoryRequestValidation.class) @RequestBody final EditCategoryRequest editCategoryRequest,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<EditCategoryResponseMessage> response = categoryService.editCategory(editCategoryRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority(T(pl.crystalek.budgetweb.household.role.permission.Permission).CATEGORY_DELETE)")
    public ResponseEntity<ResponseAPI<DeleteCategoryResponseMessage>> deleteCategory(
            @AuthenticationPrincipal final long userId,
            @PathVariable final String id
    ) {
        final ResponseAPI<DeleteCategoryResponseMessage> response = categoryService.deleteCategory(id, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
