package pl.crystalek.budgetweb.log;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;

//@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLog<T extends Enum<T>> {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    Instant timestamp;

    @Transient
    @Setter
    @Column(nullable = false)
    T actionType;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    String entityType;

    @ManyToOne
    @JoinColumn(name = "executor_user_id", nullable = false)
    User executorUser;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    Household household;

    public EventLog(final Instant timestamp, final T actionType, final String description, final Class<?> entityType, final User executorUser, final Household household) {
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.description = description;
        this.entityType = entityType.getSimpleName();
        this.executorUser = executorUser;
        this.household = household;
    }

    @Access(AccessType.PROPERTY)
    public String getActionType() {
        return actionType.name();
    }
}
