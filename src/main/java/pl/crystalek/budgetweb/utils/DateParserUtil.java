package pl.crystalek.budgetweb.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@UtilityClass
public class DateParserUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Optional<LocalDate> parseDate(String value) {
        try {
            return Optional.of(LocalDate.parse(value, DATE_FORMATTER));
        } catch (final DateTimeParseException | NullPointerException exception) {
            return Optional.empty();
        }
    }
}
