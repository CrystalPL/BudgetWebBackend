package pl.crystalek.budgetweb.receipt.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIReceiptJsonData(
        @JsonProperty("proponowana_nazwa") String proposedName,
        @JsonProperty("nazwa_produktu") String scannedName,
        @JsonProperty("ilosc") String quantity,
        @JsonProperty("cena") String price,
        @JsonProperty("suma") String total,
        @JsonProperty("kategoria") String category,
        @JsonProperty("upusty") List<String> discounts
) {}