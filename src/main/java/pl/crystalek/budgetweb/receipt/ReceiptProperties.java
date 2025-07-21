package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties("receipt")
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptProperties {
    NameInReceiptProperties shopName = new NameInReceiptProperties();
    NameInReceiptProperties productName = new NameInReceiptProperties();
}
