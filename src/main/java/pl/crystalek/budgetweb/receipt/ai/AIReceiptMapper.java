package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.receipt.ReceiptProperties;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceiptItem;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptItem;
import pl.crystalek.budgetweb.receipt.items.response.ReceiptItemCategoryDTO;
import pl.crystalek.budgetweb.user.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AIReceiptMapper {
    AIProcessedReceipt aiProcessedReceipt;
    ReceiptProperties receiptProperties;
    Set<Category> categories;

    public AIReceiptMapper(final AIProcessedReceipt aiProcessedReceipt, final ReceiptProperties receiptProperties, final User requsterUser) {
        this.aiProcessedReceipt = aiProcessedReceipt;
        this.receiptProperties = receiptProperties;
        this.categories = requsterUser.getHouseholdMember().getHousehold().getCategories();
    }

    public AIReceipt map() {
        final String shopName = receiptProperties.getShopName().getValidName(aiProcessedReceipt.shop()).orElse(null);
        final Instant shoppingTime = aiProcessedReceipt.shoppingTime();
        final List<AIReceiptItem> receiptItems = getReceiptItems();

        return new AIReceipt(shopName, shoppingTime, receiptItems);
    }

    private List<AIReceiptItem> getReceiptItems() {
        return aiProcessedReceipt.aiReceiptItems().stream()
                .map(this::getReceiptItem)
                .toList();
    }

    private AIReceiptItem getReceiptItem(final AIProcessedReceiptItem processedReceiptItem) {
        final String productName = receiptProperties.getProductName().getValidName(processedReceiptItem.productName()).orElse(null);
        final ReceiptItemCategoryDTO category = getCategory(processedReceiptItem.category());
        final Double priceAfterDiscounts = getPriceAfterDiscounts(processedReceiptItem);

        return new AIReceiptItem(productName, processedReceiptItem.quantity(), priceAfterDiscounts, category);
    }

    private ReceiptItemCategoryDTO getCategory(final String categoryName) {
        return categories.stream()
                .filter((category) -> category.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .map(category -> new ReceiptItemCategoryDTO(category.getId(), category.getName()))
                .orElse(null);
    }

    private Double getPriceAfterDiscounts(final AIProcessedReceiptItem processedReceiptItem) {
        final Double price = processedReceiptItem.price();
        final Double quantity = processedReceiptItem.quantity();
        if (price == null || quantity == null) {
            return null;
        }

        final Double sumOfDiscounts = processedReceiptItem.getSumOfDiscounts();
        final Double sum = price * quantity;
        final Double sumAfterDiscounts = sum - sumOfDiscounts;

        return sumAfterDiscounts / quantity;
    }
}
