package pl.crystalek.budgetweb.receipt.items;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.receipt.Receipt;
import pl.crystalek.budgetweb.user.model.UserData;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "receipt_items")
public class ReceiptItem {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String productName;

    @Column(nullable = false)
    Instant creationTime;

    @Column(nullable = false)
    double quantity;

    @Column(nullable = false)
    double price;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    Double dividing;

    @ManyToOne
    @JoinColumn(name = "return_user_id")
    UserData whoReturnMoney;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = false)
    Receipt receipt;

    public ReceiptItem(final long id, final String productName, final Instant creationTime, final double quantity,
                       final double price, final Category category, final Double dividing, final UserData whoReturnMoney, final Receipt receipt) {
        this.id = id;
        this.productName = productName;
        this.creationTime = creationTime;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.dividing = dividing;
        this.whoReturnMoney = whoReturnMoney;
        this.receipt = receipt;
    }

    public ReceiptItem(final String productName, final Instant creationTime, final double quantity, final double price,
                       final Category category, final Double dividing, final UserData whoReturnMoney, final Receipt receipt) {
        this.productName = productName;
        this.creationTime = creationTime;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.dividing = dividing;
        this.whoReturnMoney = whoReturnMoney;
        this.receipt = receipt;
    }
}
