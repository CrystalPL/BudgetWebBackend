package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.receipt.items.ReceiptItem;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptDetails;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptItemsData;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptRequest;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserData;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
class ReceiptCreationAssembler {
    final Household household;
    final SaveReceiptRequest requestContent;
    @Getter
    Receipt receipt;
    Set<ReceiptItem> receiptItems;

    public ReceiptCreationAssembler(final User requesterUser, final SaveReceiptRequest requestContent) {
        this.requestContent = requestContent;
        this.household = requesterUser.getHouseholdMember().getHousehold();
    }

    public void createReceipt() {
        mapToReceipt();
        mapToReceiptItems();
        receipt.setItems(receiptItems);
    }

    private void mapToReceipt() {
        final SaveReceiptDetails receiptDetails = requestContent.receiptDetails();

        final Long receiptId = receiptDetails.receiptId();
        final String shopName = receiptDetails.shopName();
        final Instant creationTime = Instant.now();
        final Instant shoppingTime = receiptDetails.date();
        final Boolean settled = receiptDetails.isSettled();
        final UserData whoPaidForReceipt = getUserById(receiptDetails.whoPaidId());

        receipt = new Receipt(receiptId, shopName, creationTime, shoppingTime, whoPaidForReceipt, household, settled);
    }

    private void mapToReceiptItems() {
        receiptItems = requestContent.itemsDataList().stream()
                .map(this::mapToReceiptItem)
                .collect(Collectors.toSet());
    }

    private ReceiptItem mapToReceiptItem(final SaveReceiptItemsData itemData) {
        final Long receiptItemId = itemData.receiptItemId();
        final String productName = itemData.productName();
        final Double quantity = itemData.quantity();
        final Double price = itemData.price();
        final Double moneyDividing = itemData.moneyDividing();
        final Instant creationTime = Instant.now();
        final Long userToReturnMoneyId = itemData.userToReturnMoneyId();
        final UserData userToReturnMoneyForProduct = userToReturnMoneyId != null ? getUserById(userToReturnMoneyId) : null;
        final Category category = household.getCategories().stream()
                .filter(householdCategory -> householdCategory.getId() == itemData.categoryId())
                .findFirst().orElseThrow(() -> new NoSuchElementException("Mimo weryfikacji istnienia kategorii w gospodarstwie, nie odnaleziono jej."));

        return new ReceiptItem(receiptItemId, productName, creationTime, quantity, price, category, moneyDividing, userToReturnMoneyForProduct, receipt);
    }

    private UserData getUserById(final Long id) {
        return household.getMembers().stream()
                .map(HouseholdMember::getUser)
                .map(User::getUserData)
                .filter(user -> user.getId() == id)
                .findFirst().orElseThrow(() -> new NoSuchElementException("Mimo weryfikacji istnienia użytkowników w gospodarstwie, nie odnaleziono użytkownika."));
    }
}
