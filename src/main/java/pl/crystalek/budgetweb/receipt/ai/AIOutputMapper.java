package pl.crystalek.budgetweb.receipt.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.receipt.ai.model.JsonPurchaseData;
import pl.crystalek.budgetweb.receipt.ai.model.result.AIOutputValidationResult;
import pl.crystalek.budgetweb.user.model.User;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AIOutputMapper {
    static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    String outputJson;
    User executor;

    public AIOutputValidationResult validateAIMessage() {
        final Optional<JsonPurchaseData> jsonPurchaseDataOptional = deserializeJsonPurchaseData();
        if (jsonPurchaseDataOptional.isEmpty()) {
            return AIOutputValidationResult.JSON_DESERIALIZATION_FAILED;
        }

        return null;
    }

    private Optional<JsonPurchaseData> deserializeJsonPurchaseData() {
        try {
            final JsonPurchaseData jsonPurchaseData = OBJECT_MAPPER.readValue(outputJson, JsonPurchaseData.class);
            return Optional.of(jsonPurchaseData);
        } catch (final JsonProcessingException exception) {
            return Optional.empty();
        }
    }
}
