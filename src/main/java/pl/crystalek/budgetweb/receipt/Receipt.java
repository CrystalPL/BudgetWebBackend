package pl.crystalek.budgetweb.receipt;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.receipt.items.ReceiptItem;
import pl.crystalek.budgetweb.user.model.UserData;

import java.time.Instant;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "receipts")
public class Receipt {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String shop;

    @Column(nullable = false)
    Instant creationTime;

    @Column(nullable = false)
    Instant shoppingTime;

    @ManyToOne
    @JoinColumn(name = "who_paid_id", nullable = false)
    UserData whoPaid;

    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    Household household;

    @Column(nullable = false)
    boolean settled;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ReceiptItem> items;

    public Receipt(final long id, final String shop, final Instant creationTime, final Instant shoppingTime, final UserData whoPaid, final Household household, final boolean settled) {
        this.id = id;
        this.shop = shop;
        this.creationTime = creationTime;
        this.shoppingTime = shoppingTime;
        this.whoPaid = whoPaid;
        this.household = household;
        this.settled = settled;
    }

    public Receipt(final String shop, final Instant creationTime, final Instant shoppingTime, final UserData whoPaid, final Household household, final boolean settled) {
        this.shop = shop;
        this.creationTime = creationTime;
        this.shoppingTime = shoppingTime;
        this.whoPaid = whoPaid;
        this.household = household;
        this.settled = settled;
    }
}
