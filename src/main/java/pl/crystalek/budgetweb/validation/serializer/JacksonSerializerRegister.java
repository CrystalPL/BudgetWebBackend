package pl.crystalek.budgetweb.validation.serializer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.validation.Validator;

@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class JacksonSerializerRegister {

    @Bean
    public Module staticFieldsModule() {
        final SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new JacksonSerializerModifier(Validator.class, new StaticFieldsSerializer<>()));
        return module;
    }
}
