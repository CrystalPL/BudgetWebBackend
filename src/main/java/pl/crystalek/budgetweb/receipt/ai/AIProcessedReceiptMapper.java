package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceiptItem;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptItemJsonData;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptJsonData;
import pl.crystalek.budgetweb.utils.NumberUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class AIProcessedReceiptMapper {
    static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    AIReceiptItemJsonData purchaseData;

    AIProcessedReceipt map() {
        final String shopName = purchaseData.shopName();
        final Instant shoppingTime = parseShoppingTime(purchaseData.purchaseDate());
        final List<AIProcessedReceiptItem> items = getItems();

        return new AIProcessedReceipt(shopName, shoppingTime, items);
    }

    private Instant parseShoppingTime(final String purchaseDate) {
        if (StringUtils.isEmpty(purchaseDate)) {
            return null;
        }

        final LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(purchaseDate.trim(), DATE_FORMATTER);
        } catch (final DateTimeParseException exception) {
            return null;
        }

        final ZonedDateTime zonedDateTime = parsedDate.atStartOfDay(ZoneId.systemDefault());
        return zonedDateTime.toInstant();
    }

    private List<AIProcessedReceiptItem> getItems() {
        final List<AIReceiptJsonData> products = purchaseData.products();
        if (products == null) {
            return List.of();
        }

        return products.stream()
                .map(this::mapItem)
                .toList();
    }

    private AIProcessedReceiptItem mapItem(final AIReceiptJsonData product) {
        final String shopName = resolveProductName(product);
        final Double quantity = NumberUtil.getDouble(product.quantity()).orElse(null);
        final Double price = NumberUtil.getDouble(product.price()).orElse(null);
        final String category = product.category();
        final List<Double> discounts = parseDiscounts(product);

        return new AIProcessedReceiptItem(shopName, quantity, price, category, discounts);
    }

    private String resolveProductName(final AIReceiptJsonData product) {
        final String proposedName = product.proposedName();
        if (StringUtils.isNotEmpty(proposedName)) {
            return proposedName;
        }

        final String scannedName = product.scannedName();
        return scannedName != null ? scannedName.trim() : null;
    }

    private List<Double> parseDiscounts(final AIReceiptJsonData product) {
        final List<String> discounts = product.discounts();
        if (discounts == null) {
            return List.of();
        }

        return discounts.stream()
                .map(NumberUtil::getDouble)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
