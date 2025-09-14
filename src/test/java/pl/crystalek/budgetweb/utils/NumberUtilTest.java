package pl.crystalek.budgetweb.utils;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NumberUtilTest {

    @Test
    void shouldReturnLongWhenValid() {
        final Optional<Long> value = NumberUtil.getLong("123456789");
        assertThat(value).isPresent().contains(123456789L);
    }

    @Test
    void shouldReturnEmptyForLongWhenInvalidOrNull() {
        assertThat(NumberUtil.getLong("abc")).isEmpty();
        assertThat(NumberUtil.getLong(null)).isEmpty();
    }

    @Test
    void shouldReturnShortWhenValid() {
        final Optional<Short> value = NumberUtil.getShort("12345");
        assertThat(value).isPresent().contains((short) 12345);
    }

    @Test
    void shouldReturnEmptyForShortWhenInvalidOrNull() {
        assertThat(NumberUtil.getShort("40000")).isEmpty();
        assertThat(NumberUtil.getShort(null)).isEmpty();
    }

    @Test
    void shouldReturnIntWhenValid() {
        final Optional<Integer> value = NumberUtil.getInt("2147483647");
        assertThat(value).isPresent().contains(2147483647);
    }

    @Test
    void shouldReturnEmptyForIntWhenInvalidOrNull() {
        assertThat(NumberUtil.getInt("x1")).isEmpty();
        assertThat(NumberUtil.getInt(null)).isEmpty();
    }

    @Test
    void shouldReturnDoubleWhenValid() {
        final Optional<Double> value = NumberUtil.getDouble("3.1415");
        assertThat(value).isPresent().contains(3.1415);
    }

    @Test
    void shouldReturnEmptyForDoubleWhenInvalidOrNull() {
        assertThat(NumberUtil.getDouble("NaNabc")).isEmpty();
        assertThat(NumberUtil.getDouble(null)).isEmpty();
    }

    @Test
    void shouldReturnFloatWhenValid() {
        final Optional<Float> value = NumberUtil.getFloat("2.5");
        assertThat(value).isPresent().contains(2.5f);
    }

    @Test
    void shouldReturnEmptyForFloatWhenInvalidOrNull() {
        assertThat(NumberUtil.getFloat("float")).isEmpty();
        assertThat(NumberUtil.getFloat(null)).isEmpty();
    }
}