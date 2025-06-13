package pl.crystalek.budgetweb.category;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.category.response.GetCategoryResponse;

import java.util.Set;

interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query("SELECT c.id FROM Receipt c WHERE c.id IN :ids")
    Set<Long> findExistingIds(final Set<Long> ids);

    @Query("""
            SELECT new pl.crystalek.budgetweb.category.response.GetCategoryResponse(
                c.id,
                c.name,
                c.color
            )
            FROM Category c
                JOIN HouseholdMember hm ON hm.user.id = :userId
                JOIN Household h ON hm.household.id = h.id
            WHERE c.household.id = hm.household.id
            """)
    Set<GetCategoryResponse> getCategoriesByUserId(final long userId);
}
