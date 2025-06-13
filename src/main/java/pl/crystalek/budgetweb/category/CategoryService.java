package pl.crystalek.budgetweb.category;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.category.request.CreateCategoryRequest;
import pl.crystalek.budgetweb.category.request.EditCategoryRequest;
import pl.crystalek.budgetweb.category.response.CreateCategoryResponseMessage;
import pl.crystalek.budgetweb.category.response.DeleteCategoryResponseMessage;
import pl.crystalek.budgetweb.category.response.EditCategoryResponseMessage;
import pl.crystalek.budgetweb.category.response.GetCategoryResponse;
import pl.crystalek.budgetweb.household.CreateHouseholdEvent;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CategoryService {
    CategoryRepository repository;
    UserService userService;

    public Set<Long> findExistingIds(final Set<Long> ids) {
        return repository.findExistingIds(ids);
    }

    @EventListener
    @Order(10)
    public void addDefaultCategoriesToHousehold(final CreateHouseholdEvent event) {
        final Household household = event.household();

        final Category category1 = new Category(household, "Artykuły spożywcze", "#E6994D", Instant.now());
        final Category category2 = new Category(household, "Rozrywka", "#FF00FF", Instant.now());

        repository.saveAll(Set.of(category1, category2));
    }

    public Set<GetCategoryResponse> getCategories(final long userId) {
        return repository.getCategoriesByUserId(userId);
    }

    public ResponseAPI<EditCategoryResponseMessage> editCategory(final EditCategoryRequest request, final long requesterId) {
        final Optional<Category> categoryOptional = repository.findById(request.getCategoryId());
        if (categoryOptional.isEmpty()) {
            return new ResponseAPI<>(false, EditCategoryResponseMessage.CATEGORY_NOT_FOUND);
        }

        final Category category = categoryOptional.get();
        final boolean anotherHousehold = category.getHousehold().getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (anotherHousehold) {
            return new ResponseAPI<>(false, EditCategoryResponseMessage.NOT_YOUR_HOUSEHOLD);
        }

        category.setName(request.name());
        category.setColor(request.color());

        try {
            repository.save(category);
        } catch (final DataIntegrityViolationException exception) {
            return new ResponseAPI<>(false, EditCategoryResponseMessage.CATEGORY_EXISTS);
        }

        return new ResponseAPI<>(true, EditCategoryResponseMessage.SUCCESS);
    }

    public ResponseAPI<CreateCategoryResponseMessage> createCategory(final CreateCategoryRequest request, final long requesterId) {
        final Household household = userService.getUserById(requesterId).get().getHouseholdMember().getHousehold();
        final Category category = new Category(household, request.name(), request.color(), Instant.now());

        try {
            repository.save(category);
        } catch (final DataIntegrityViolationException exception) {
            return new ResponseAPI<>(false, CreateCategoryResponseMessage.CATEGORY_EXISTS);
        }

        return new ResponseAPI<>(true, CreateCategoryResponseMessage.SUCCESS);
    }

    public ResponseAPI<DeleteCategoryResponseMessage> deleteCategory(final String stringCategoryId, final long requesterId) {
        if (stringCategoryId == null || stringCategoryId.isBlank()) {
            return new ResponseAPI<>(false, DeleteCategoryResponseMessage.MISSING_CATEGORY_ID);
        }

        final long categoryId;
        try {
            categoryId = Long.parseLong(stringCategoryId);
        } catch (final NumberFormatException exception) {
            return new ResponseAPI<>(false, DeleteCategoryResponseMessage.ERROR_NUMBER_FORMAT);
        }

        final Optional<Category> categoryOptional = repository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteCategoryResponseMessage.CATEGORY_NOT_FOUND);
        }

        final Category category = categoryOptional.get();
        final Household household = category.getHousehold();

        final boolean anotherHousehold = household.getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (anotherHousehold) {
            return new ResponseAPI<>(false, DeleteCategoryResponseMessage.NOT_YOUR_HOUSEHOLD);
        }

        repository.delete(category);
        return new ResponseAPI<>(true, DeleteCategoryResponseMessage.SUCCESS);
    }
}
