package pl.crystalek.budgetweb.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ValidationEntityType {
    @JsonProperty("email")
    EMAIL,
    @JsonProperty("password")
    PASSWORD,
    @JsonProperty("username")
    USERNAME,
    @JsonProperty("household")
    HOUSEHOLD,
    @JsonProperty("category")
    CATEGORY,
    @JsonProperty("receipt")
    RECEIPT,
    @JsonProperty("receiptItem")
    RECEIPT_ITEM,
}