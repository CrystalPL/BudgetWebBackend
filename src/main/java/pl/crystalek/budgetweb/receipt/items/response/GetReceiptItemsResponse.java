package pl.crystalek.budgetweb.receipt.items.response;

import pl.crystalek.budgetweb.receipt.response.UserWhoPaid;

public record GetReceiptItemsResponse(long id, String productName, double quantity, double price,
                                      ReceiptItemCategoryDTO category, Double moneyDividing,
                                      UserWhoPaid userToReturnMoney) {
    public GetReceiptItemsResponse(final long id, final String productName, final double quantity, final double price,
                                   final ReceiptItemCategoryDTO category, final Double moneyDividing, final Long userId, final String userName) {
        this(id, productName, quantity, price, category, moneyDividing, userId != null && userName != null ? new UserWhoPaid(userId, userName) : null);
    }
}
