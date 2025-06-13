package pl.crystalek.budgetweb.user.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.receipt.Receipt;
import pl.crystalek.budgetweb.receipt.items.ReceiptItem;

import java.util.Set;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
public class UserData {
    @Id
    long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @MapsId
    User user;

    @Setter
    @Column(nullable = false)
    String nickname;

    @OneToMany(mappedBy = "whoPaid", cascade = CascadeType.ALL)
    Set<Receipt> receipts;

    @OneToMany(mappedBy = "whoReturnMoney", cascade = CascadeType.ALL)
    Set<ReceiptItem> itemsToReturnMoney;

    public UserData(final User user, final String nickname) {
        this.user = user;
        this.nickname = nickname;
    }
}
