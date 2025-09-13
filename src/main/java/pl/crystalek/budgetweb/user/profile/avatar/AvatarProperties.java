package pl.crystalek.budgetweb.user.profile.avatar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.util.Set;

@Configuration
@ConfigurationProperties("avatar")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class AvatarProperties {
    DataSize maxAvatarSize;
    Set<String> allowedAvatarExtensions;
}
