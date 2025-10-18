package pl.crystalek.budgetweb.validation;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ValidationService {
    Map<ValidationEntityType, Validator> validators;

    ValidationService(final List<Validator> validators) {
        this.validators = validators.stream().collect(Collectors.toMap(Validator::getEntityType, this::createNonBeanValidator));
    }

    @SneakyThrows
    private Validator createNonBeanValidator(final Validator validator) {
        final Class<?> userClass = ClassUtils.getUserClass(validator);
        final Constructor<?> declaredConstructor = userClass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        return (Validator) declaredConstructor.newInstance();
    }

    Map<ValidationEntityType, Validator> getValidators(final List<ValidationEntityType> entityTypes) {
        return entityTypes.stream().collect(Collectors.toMap(Function.identity(), validators::get));
    }
}
