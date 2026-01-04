package pl.crystalek.budgetweb.filter.condition;

import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.filter.condition.model.Condition;

interface ConditionRepository extends CrudRepository<Condition, Long> {
}
