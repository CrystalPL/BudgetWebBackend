package pl.crystalek.budgetweb.receipt.response;

import java.time.Instant;

public record GetReceiptResponse(long id, String shop, Instant shoppingTime, double receiptAmount, UserWhoPaid whoPaid,
                                 boolean settled) {
}
