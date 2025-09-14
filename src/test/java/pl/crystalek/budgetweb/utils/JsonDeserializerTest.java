package pl.crystalek.budgetweb.utils;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JsonDeserializerTest {

    @Test
    void shouldDeserializeJson() {
        final String json = "{\"name\":\"Jan\",\"age\":30}";
        final Optional<Person> result = JsonDeserializer.deserializeJson(json, Person.class);

        assertThat(result).isPresent();
        final Person person = result.orElseThrow();
        assertThat(person.name).isEqualTo("Jan");
        assertThat(person.age).isEqualTo(30);
    }

    @Test
    void shouldReturnEmptyForInvalidJson() {
        final String invalid = "{name:\"Jan\",age:30";
        final Optional<Person> result = JsonDeserializer.deserializeJson(invalid, Person.class);

        assertThat(result).isEmpty();
    }

    public static class Person {
        public String name;
        public int age;

        public Person() {
        }
    }
}