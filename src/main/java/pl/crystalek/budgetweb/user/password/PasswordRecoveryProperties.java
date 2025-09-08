package pl.crystalek.budgetweb.user.password;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.crystalek.budgetweb.email.EmailProperties;

@Configuration
@ConfigurationProperties("password-recovery.email.config")
class PasswordRecoveryProperties extends EmailProperties {
}
