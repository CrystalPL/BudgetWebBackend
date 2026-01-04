package pl.crystalek.budgetweb.filter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.crystalek.budgetweb.filter.response.AdvancedFilterListGetterResponse;

import java.util.List;
import java.util.Optional;

interface AdvancedFilterRepository extends CrudRepository<AdvancedFilter, Long> {

    //return true when filter exists
    @Query(value = "CALL activate_filter(:p_id, :p_requester_id)", nativeQuery = true)
    boolean activateFilter(@Param("p_id") final long id, @Param("p_requester_id") final long userId);

    Optional<AdvancedFilter> findByIdAndUser_Id(final long filterId, final long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.filter.response.AdvancedFilterListGetterResponse(
                f.id,
                f.filterName,
                f.description,
                f.active,
                f.createdAt,
                f.updatedAt,
                CAST(COALESCE(SUM(SIZE(cg.conditions)), 0L) AS long),
                CAST(COUNT(cg) AS long)
            )
            FROM AdvancedFilter f
            LEFT JOIN f.conditionGroups cg
            WHERE f.user.id = :userId
            AND f.fieldType = :fieldType
            GROUP BY f.id, f.filterName, f.description, f.active, f.createdAt, f.updatedAt
            """)
    List<AdvancedFilterListGetterResponse> findByFieldTypeAndUserId(
            @Param("fieldType") AdvancedFilterEntityType fieldType,
            @Param("userId") long userId
    );
}
