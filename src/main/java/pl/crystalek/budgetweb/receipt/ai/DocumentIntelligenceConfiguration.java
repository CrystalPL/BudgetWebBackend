package pl.crystalek.budgetweb.receipt.ai;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.core.credential.KeyCredential;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("azure.document-intelligence")
@FieldDefaults(level = AccessLevel.PRIVATE)
class DocumentIntelligenceConfiguration {
    String endpoint;
    String apiKey;

    @Bean
    DocumentIntelligenceClient documentIntelligenceClient() {
        return new DocumentIntelligenceClientBuilder()
                .endpoint(endpoint)
                .credential(new KeyCredential(apiKey))
                .buildClient();
    }
}
