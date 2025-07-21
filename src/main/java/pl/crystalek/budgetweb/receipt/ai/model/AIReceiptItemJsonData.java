package pl.crystalek.budgetweb.receipt.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AIReceiptItemJsonData(
        @JsonProperty("nazwa_sklepu") String shopName,
        @JsonProperty("data_zakupow") String purchaseDate,
        @JsonProperty("produkty") List<AIReceiptJsonData> products
) {
}
