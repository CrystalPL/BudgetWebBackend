package pl.crystalek.budgetweb.email;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class EmailProperties {
    String from;
    String returnAddress;
    String messageSubject;
    String message;
    Duration emailExpireTime;
    Duration cleanUpExpiredEmails;
}
