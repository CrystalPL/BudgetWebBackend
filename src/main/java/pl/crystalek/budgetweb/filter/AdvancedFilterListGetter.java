package pl.crystalek.budgetweb.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AdvancedFilterListGetter {
}
