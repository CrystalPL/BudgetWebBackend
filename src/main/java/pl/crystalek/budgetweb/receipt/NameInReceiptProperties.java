package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NameInReceiptProperties {
    Integer minLength = 1;
    Integer maxLength = 64;
    List<String> illegalWords = List.of();

    public Optional<String> getValidName(final String name) {
        if (name == null) {
            return Optional.empty();
        }

        final int productNameLength = name.length();
        if (productNameLength < minLength || productNameLength > maxLength) {
            return Optional.empty();
        }

        final boolean containsIllegalWord = StringUtils.containsAnyIgnoreCase(name, illegalWords.toArray(new String[0]));
        return containsIllegalWord ? Optional.empty() : Optional.of(name);
    }
}
