package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties("ai.receipt")
@FieldDefaults(level = AccessLevel.PRIVATE)
class AIProperties {
    DataSize maxPhotoSize;
    Set<String> allowedPhotoExtensions;
    String prompt;
}
