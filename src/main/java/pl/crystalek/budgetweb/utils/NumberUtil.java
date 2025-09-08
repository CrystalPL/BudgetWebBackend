package pl.crystalek.budgetweb.utils;

import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * The NumberUtil class provides utility methods for converting numbers.
 */
@UtilityClass
public class NumberUtil {

    /**
     * Attempts to convert the specified object to a {@link Long}.
     *
     * @param number the object to convert
     * @return an {@link Optional} containing the Long value if the conversion was successful, or an empty Optional if not
     */
    public Optional<Long> getLong(final Object number) {
        try {
            return Optional.of(Long.parseLong(number.toString()));
        } catch (final NumberFormatException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to convert the specified object to a {@link Short}.
     *
     * @param number the object to convert
     * @return an {@link Optional} containing the Short value if the conversion was successful, or an empty Optional if not
     */
    public Optional<Short> getShort(final Object number) {
        try {
            return Optional.of(Short.parseShort(number.toString()));
        } catch (final NumberFormatException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to convert the specified object to an {@link Integer}.
     *
     * @param number the object to convert
     * @return an {@link Optional} containing the Integer value if the conversion was successful, or an empty Optional if not
     */
    public Optional<Integer> getInt(final Object number) {
        try {
            return Optional.of(Integer.parseInt(number.toString()));
        } catch (final NumberFormatException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to convert the specified object to a {@link Double}.
     *
     * @param number the object to convert
     * @return an {@link Optional} containing the Double value if the conversion was successful, or an empty Optional if not
     */
    public Optional<Double> getDouble(final Object number) {
        try {
            return Optional.of(Double.parseDouble(number.toString()));
        } catch (final NumberFormatException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    /**
     * Attempts to convert the specified object to a {@link Float}.
     *
     * @param number the object to convert
     * @return an {@link Optional} containing the Float value if the conversion was successful, or an empty Optional if not
     */
    public Optional<Float> getFloat(final Object number) {
        try {
            return Optional.of(Float.parseFloat(number.toString()));
        } catch (final NumberFormatException | NullPointerException exception) {
            return Optional.empty();
        }
    }
}