package pl.crystalek.budgetweb.receipt.ai;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.models.AnalyzeDocumentOptions;
import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.AnalyzedDocument;
import com.azure.ai.documentintelligence.models.CurrencyValue;
import com.azure.ai.documentintelligence.models.DocumentField;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceiptItem;
import pl.crystalek.budgetweb.receipt.ai.model.ReceiptAnalysisResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ReceiptDocumentAnalyzer {
    static String RECEIPT_MODEL_ID = "prebuilt-receipt";
    DocumentIntelligenceClient documentIntelligenceClient;

    ReceiptAnalysisResult analyze(final File imageFile) throws IOException {
        final byte[] content = Files.readAllBytes(imageFile.toPath());
        final AnalyzeResult result = documentIntelligenceClient
                .beginAnalyzeDocument(RECEIPT_MODEL_ID, new AnalyzeDocumentOptions(content))
                .getFinalResult();

        final String rawText = result.getContent();
        final List<AnalyzedDocument> documents = result.getDocuments();
        if (documents == null || documents.isEmpty()) {
            return new ReceiptAnalysisResult(new AIProcessedReceipt(null, null, List.of()), rawText);
        }

        final Map<String, DocumentField> fields = documents.get(0).getFields();
        final String shopName = readString(fields.get("MerchantName"));
        final Instant shoppingTime = readShoppingTime(fields.get("TransactionDate"));
        final List<AIProcessedReceiptItem> items = readItems(fields.get("Items"));

        return new ReceiptAnalysisResult(new AIProcessedReceipt(shopName, shoppingTime, items), rawText);
    }

    private List<AIProcessedReceiptItem> readItems(final DocumentField itemsField) {
        if (itemsField == null || itemsField.getValueList() == null) {
            return List.of();
        }

        final List<AIProcessedReceiptItem> items = new ArrayList<>();
        for (final DocumentField itemField : itemsField.getValueList()) {
            final Map<String, DocumentField> item = itemField.getValueMap();
            if (item == null) {
                continue;
            }

            final String productName = readString(item.get("Description"));
            //gdy DI nie wykryje ilości, przyjmujemy 1 sztukę — inaczej cena zostałaby odrzucona w dalszym mapowaniu
            final Double quantity = readNumber(item.get("Quantity"), 1.0);
            final Double price = readUnitPrice(item.get("Price"), item.get("TotalPrice"), quantity);

            //pomijamy widmowe pozycje bez ceny (DI czasem rozbija nazwę produktu na dwie linie -> jedna bez wartości)
            if (price == null) {
                continue;
            }

            //kategoria zostanie uzupełniona przez ReceiptCategoryAssigner; prebuilt-receipt nie zwraca rabatów per pozycja
            items.add(new AIProcessedReceiptItem(productName, quantity, price, null, List.of()));
        }

        return items;
    }

    private Double readUnitPrice(final DocumentField priceField, final DocumentField totalPriceField, final Double quantity) {
        final Double unitPrice = readCurrency(priceField);
        if (unitPrice != null) {
            return unitPrice;
        }

        final Double totalPrice = readCurrency(totalPriceField);
        if (totalPrice == null) {
            return null;
        }

        if (quantity == null || quantity == 0) {
            return totalPrice;
        }

        return totalPrice / quantity;
    }

    private Double readNumber(final DocumentField field, final double defaultValue) {
        if (field == null || field.getValueNumber() == null) {
            return defaultValue;
        }

        return field.getValueNumber();
    }

    private Double readCurrency(final DocumentField field) {
        if (field == null) {
            return null;
        }

        final CurrencyValue currency = field.getValueCurrency();
        return currency != null ? currency.getAmount() : null;
    }

    private String readString(final DocumentField field) {
        return field != null ? field.getValueString() : null;
    }

    private Instant readShoppingTime(final DocumentField field) {
        if (field == null) {
            return null;
        }

        final LocalDate date = field.getValueDate();
        return date != null ? date.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
    }
}
