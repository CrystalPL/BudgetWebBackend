package pl.crystalek.budgetweb.user.profile.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.crystalek.budgetweb.email.EmailProperties;

@Configuration
@ConfigurationProperties("change-email.email.config")
class ChangeEmailProperties extends EmailProperties {
}
