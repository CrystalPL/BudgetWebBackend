package pl.crystalek.budgetweb.receipt.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JsonProduct(
        @JsonProperty("proponowana_nazwa") String proposedName,
        @JsonProperty("odczytana_nazwa") String scannedName,
        @JsonProperty("ilosc") String quantity,
        @JsonProperty("cena") String price,
        @JsonProperty("suma") String total,
        @JsonProperty("upusty") List<String> discounts
) {}