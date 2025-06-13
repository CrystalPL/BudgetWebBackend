package pl.crystalek.budgetweb.auth.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties("security.jwt")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenProperties {
    String cookieName;
    String secretKey;
    Duration tokenAccessTime;
    Duration refreshTokenExpireTime;
}
