package pl.crystalek.budgetweb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class JsonDeserializer {
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public <T> Optional<T> deserializeJson(final String json, final Class<T> clazz) {
        try {
            final T value = OBJECT_MAPPER.readValue(json, clazz);
            return Optional.of(value);
        } catch (final JsonProcessingException exception) {
            return Optional.empty();
        }
    }
}
