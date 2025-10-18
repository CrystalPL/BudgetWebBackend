package pl.crystalek.budgetweb.validation.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class JacksonSerializerModifier extends BeanSerializerModifier {
    Class<?> targetInterface;
    JsonSerializer<?> serializer;

    @Override
    public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        if (targetInterface.isAssignableFrom(beanDesc.getBeanClass())) {
            return this.serializer;
        }

        return serializer;
    }
}
