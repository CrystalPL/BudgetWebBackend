package pl.crystalek.budgetweb.filter;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface AdvancedFilterRepository extends CrudRepository<AdvancedFilter, Long> {

    //return true when filter exists
    @Procedure(name = "activate_filter")
    boolean activateFilter(@Param("p_id") final long id, @Param("p_requester_id") final long userId);

    Optional<AdvancedFilter> findByIdAndUser_Id(final long filterId, final long userId);
}
