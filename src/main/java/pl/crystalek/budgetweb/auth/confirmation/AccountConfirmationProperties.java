package pl.crystalek.budgetweb.auth.confirmation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties("account.confirmation.email.config")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class AccountConfirmationProperties {
    String from;
    String returnConfirmAddress;
    String messageSubject;
    String message;
    Duration emailExpireTime;
    Duration cleanUpExpiredEmails;
}
