package pl.crystalek.budgetweb.validation.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.crystalek.budgetweb.validation.Validator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class StaticFieldsSerializer<T extends Validator> extends JsonSerializer<T> {

    @Override
    public void serialize(final T value, final JsonGenerator jsonGenerator, final SerializerProvider serializers) throws IOException {
        jsonGenerator.writeStartObject();

        Arrays.stream(value.getClass().getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .forEach(field -> writeField(jsonGenerator, field));

        jsonGenerator.writeEndObject();
    }

    private void writeField(final JsonGenerator jsonGenerator, final Field field) {
        field.setAccessible(true);
        try {
            final String fieldNameInCamelCase = toCamelCase(field.getName());
            jsonGenerator.writeObjectField(fieldNameInCamelCase, field.get(null));
        } catch (final IllegalAccessException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String toCamelCase(final String fieldName) {
        final StringBuilder stringBuilder = new StringBuilder();
        boolean nextUpper = false;

        for (char c : fieldName.toLowerCase().toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                stringBuilder.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }
}
