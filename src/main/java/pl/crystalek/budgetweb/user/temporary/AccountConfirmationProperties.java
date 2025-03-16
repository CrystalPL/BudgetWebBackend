package pl.crystalek.budgetweb.user.temporary;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.crystalek.budgetweb.email.EmailProperties;

@Configuration
@ConfigurationProperties("account.confirmation.email.config")
class AccountConfirmationProperties extends EmailProperties {
}
