package pl.crystalek.budgetweb.receipt.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReceiptCategorization(
        @JsonProperty("produkty") List<CategorizedProduct> products,
        @JsonProperty("kaucje") List<Deposit> deposits
) {

    /**
     * Wzbogacenie istniejącej pozycji z Document Intelligence (dopasowanie po indeksie).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CategorizedProduct(
            @JsonProperty("index") Integer index,
            @JsonProperty("proponowana_nazwa") String proposedName,
            @JsonProperty("kategoria") String category,
            @JsonProperty("rabat") Double discount
    ) {
    }

    /**
     * Kaucja/opakowanie zwrotne wyciągnięte z OCR — nowa pozycja, której DI nie zwraca.
     * Cena jednostkowa może być ujemna (przyjęcie/zwrot opakowania).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Deposit(
            @JsonProperty("nazwa") String name,
            @JsonProperty("ilosc") Double quantity,
            @JsonProperty("cena") Double price,
            @JsonProperty("kategoria") String category
    ) {
    }
}
