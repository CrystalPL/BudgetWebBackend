package pl.crystalek.budgetweb.chat.message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.chat.message.read.MessageRead;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String content;
    Instant timestamp;

    @ManyToOne
    User sender;

    @ManyToOne
    Household household;

    @OneToMany(mappedBy = "message")
    List<MessageRead> readReceipts = new ArrayList<>();
}
