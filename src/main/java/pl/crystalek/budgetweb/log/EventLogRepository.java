package pl.crystalek.budgetweb.log;

import org.springframework.data.repository.CrudRepository;

interface EventLogRepository extends CrudRepository<EventLog, Long> {
}
