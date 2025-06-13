package pl.crystalek.budgetweb.receipt.response;

import java.util.Set;

public record CreateReceiptDetailsResponse(Set<UserWhoPaid> whoPaidLists, Set<ShopOccurrence> shopOccurrences) {

}
