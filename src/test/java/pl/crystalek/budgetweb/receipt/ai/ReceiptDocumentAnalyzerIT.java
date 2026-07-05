package pl.crystalek.budgetweb.receipt.ai;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.AnalyzeDocumentOptions;
import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.AnalyzedDocument;
import com.azure.ai.documentintelligence.models.CurrencyValue;
import com.azure.ai.documentintelligence.models.DocumentField;
import com.azure.core.credential.KeyCredential;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test integracyjny uderzający realnie w Azure Document Intelligence prawdziwym zdjęciem paragonu.
 * Wymaga dedykowanego zasobu DI (endpoint + klucz w env lub w src/main/resources/.env).
 * Bez tych danych test jest pomijany, żeby nie wywalać zwykłego `gradlew test`.
 */
class ReceiptDocumentAnalyzerIT {
    static final Path IMAGE = Path.of("IMG_0119.jpg");

    @Test
    void analyzeRealReceipt() throws IOException {
        final String endpoint = readValue("AZURE_DOCUMENT_INTELLIGENCE_ENDPOINT");
        final String key = readValue("AZURE_DOCUMENT_INTELLIGENCE_KEY");
        Assumptions.assumeTrue(isUsable(endpoint) && isUsable(key),
                "Brak danych zasobu Document Intelligence (AZURE_DOCUMENT_INTELLIGENCE_ENDPOINT / _KEY) — test pominięty");

        System.out.println("=== Endpoint: " + endpoint);
        System.out.println("=== Klucz (skrót): " + key.substring(0, 6) + "..." + key.substring(key.length() - 4));
        System.out.println("=== Zdjęcie istnieje: " + Files.exists(IMAGE) + " (" + IMAGE.toAbsolutePath() + ")");

        final byte[] content = Files.readAllBytes(IMAGE);
        System.out.println("=== Rozmiar zdjęcia: " + content.length + " bajtów");

        final DocumentIntelligenceClient client = new DocumentIntelligenceClientBuilder()
                .endpoint(endpoint)
                .credential(new KeyCredential(key))
                .buildClient();

        System.out.println("=== Wysyłam do prebuilt-receipt...");
        final long start = System.currentTimeMillis();
        final AnalyzeResult result = client
                .beginAnalyzeDocument("prebuilt-receipt", new AnalyzeDocumentOptions(content))
                .getFinalResult();
        System.out.println("=== Odpowiedź po " + (System.currentTimeMillis() - start) + " ms");

        final String ocr = result.getContent();
        System.out.println("=== PELNY OCR START ===\n" + ocr + "\n=== PELNY OCR END ===");

        final List<AnalyzedDocument> documents = result.getDocuments();
        System.out.println("=== Liczba wykrytych dokumentów: " + (documents == null ? "null" : documents.size()));

        assertNotNull(documents, "getDocuments() zwróciło null");
        assertFalse(documents.isEmpty(), "DI nie wykryło żadnego dokumentu-paragonu");

        final Map<String, DocumentField> fields = documents.get(0).getFields();
        System.out.println("=== Klucze pól: " + fields.keySet());
        System.out.println("=== MerchantName: " + valueString(fields.get("MerchantName")));
        System.out.println("=== TransactionDate: " + (fields.get("TransactionDate") == null ? null : fields.get("TransactionDate").getValueDate()));

        final DocumentField itemsField = fields.get("Items");
        if (itemsField != null && itemsField.getValueList() != null) {
            int index = 0;
            for (final DocumentField itemField : itemsField.getValueList()) {
                final Map<String, DocumentField> item = itemField.getValueMap();
                if (item == null) {
                    continue;
                }
                System.out.printf("  [%d] desc=%s | qty=%s | price=%s | total=%s%n",
                        index++,
                        valueString(item.get("Description")),
                        item.get("Quantity") == null ? null : item.get("Quantity").getValueNumber(),
                        currency(item.get("Price")),
                        currency(item.get("TotalPrice")));
            }
        } else {
            System.out.println("=== Brak pola Items!");
        }
    }

    private boolean isUsable(final String value) {
        return value != null && !value.isBlank() && !value.contains("placeholder");
    }

    private String valueString(final DocumentField field) {
        return field == null ? null : field.getValueString();
    }

    private Double currency(final DocumentField field) {
        if (field == null) {
            return null;
        }
        final CurrencyValue value = field.getValueCurrency();
        return value == null ? null : value.getAmount();
    }

    private String readValue(final String name) throws IOException {
        final String fromEnv = System.getenv(name);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.replace("\"", "").trim();
        }

        final Path envFile = Path.of("src/main/resources/.env");
        if (!Files.exists(envFile)) {
            return null;
        }

        for (final String line : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
            if (line.startsWith(name + "=")) {
                return line.substring((name + "=").length()).replace("\"", "").trim();
            }
        }

        return null;
    }
}
