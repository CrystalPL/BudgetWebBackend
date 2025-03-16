package pl.crystalek.budgetweb.log;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.user.User;

/**
 * Klasa ta reprezentuje użytkownika w dzienniku wydarzeń. Powstała ona,
 * aby nawet po usunięciu konta przez użytkownika, dalej było wiadomo, kto wykonał daną akcję,
 * np. usunął kogoś z gospodarstwa domowego. Jej obiekt jest tworzony wraz z obiektem {@link User}.
 * Zmiana pseudonimu użytkownika jest od razu rejestrowana w tejże klasie.
 */
//@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EventLogUser {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(unique = true)
    long userId;

    @Setter
    @Column(nullable = false)
    String nickname;
}
