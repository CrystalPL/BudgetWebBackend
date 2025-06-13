package pl.crystalek.budgetweb.log;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;

@Getter
@Builder
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EventLogBuilder<T extends Enum<T>, E> {
    @Builder.Default
    Instant timestamp = Instant.now();
    T actionType;
    String description;
    Class<E> entityType;
    User executorUser;
    Household household;

    public EventLog<T> build() {
        return new EventLog<>(timestamp, actionType, description, entityType, executorUser, household);
    }
}