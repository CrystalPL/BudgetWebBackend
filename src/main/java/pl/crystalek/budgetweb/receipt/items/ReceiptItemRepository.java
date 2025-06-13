package pl.crystalek.budgetweb.receipt.items;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.receipt.items.response.GetProductListResponse;
import pl.crystalek.budgetweb.receipt.items.response.GetReceiptItemsResponse;
import pl.crystalek.budgetweb.receipt.items.response.SuggestCategoryResponse;

import java.util.Set;

interface ReceiptItemRepository extends CrudRepository<ReceiptItem, Long> {

    @Query("""
            SELECT new pl.crystalek.budgetweb.receipt.items.response.SuggestCategoryResponse(ri.category.id)
            FROM ReceiptItem ri
                     JOIN HouseholdMember hm ON hm.household = ri.receipt.household
            WHERE ri.productName LIKE CONCAT(:productName, '%')
              AND hm.user.id = :userId
            GROUP BY ri.category.id, ri.category.name
            ORDER BY COUNT(ri.category) DESC
            LIMIT 1
            """)
    SuggestCategoryResponse suggestCategory(final String productName, final long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.receipt.items.response.GetProductListResponse(ri.productName)
            FROM ReceiptItem ri
                        JOIN HouseholdMember hm ON hm.household = ri.receipt.household
            WHERE hm.user.id = :userId
            """)
    Set<GetProductListResponse> getProductList(final long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.receipt.items.response.GetReceiptItemsResponse(
                       ri.id, ri.productName, ri.quantity, ri.price,
                       new pl.crystalek.budgetweb.receipt.items.response.ReceiptItemCategoryDTO(ri.category.id, ri.category.name), ri.dividing,
                       wm.id, wm.nickname
            )
            FROM ReceiptItem ri
                     JOIN HouseholdMember hm ON hm.household = ri.receipt.household
                     LEFT JOIN ri.whoReturnMoney wm
            WHERE hm.user.id = :requesterId
              AND ri.receipt.id = :receiptId
            """)
    Set<GetReceiptItemsResponse> getReceiptItemByReceiptId(final long receiptId, final long requesterId);
}
