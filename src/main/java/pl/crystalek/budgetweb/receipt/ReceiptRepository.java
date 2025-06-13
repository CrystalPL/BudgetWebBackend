package pl.crystalek.budgetweb.receipt;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.receipt.response.GetReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.ShopOccurrence;
import pl.crystalek.budgetweb.receipt.response.UserWhoPaid;

import java.util.Set;

interface ReceiptRepository extends CrudRepository<Receipt, Long> {

    @Query("""
            SELECT new pl.crystalek.budgetweb.receipt.response.GetReceiptResponse(
            r.id, r.shop, r.shoppingTime, SUM(ri.price * ri.quantity),
                        new pl.crystalek.budgetweb.receipt.response.UserWhoPaid(r.whoPaid.id, r.whoPaid.nickname), r.settled
            )
            FROM Receipt r
                     JOIN HouseholdMember hm ON hm.household = r.household
                     JOIN ReceiptItem ri ON ri.receipt = r
            WHERE hm.user.id = :userId
            GROUP BY r.id, r.shop, r.shoppingTime, r.whoPaid.nickname, r.settled
            """)
    Set<GetReceiptResponse> getReceiptsByUserId(final Long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.receipt.response.UserWhoPaid(hm2.user.id, hm2.user.userData.nickname)
            FROM HouseholdMember hm
                        JOIN HouseholdMember hm2 ON hm.household = hm2.household
            WHERE hm.user.id = :userId
            """)
    Set<UserWhoPaid> getWhoPaidList(final long userId);

    @Query("""
            SELECT new pl.crystalek.budgetweb.receipt.response.ShopOccurrence(r.shop, COUNT(r.shop))
            FROM Receipt r
                     JOIN HouseholdMember hm ON hm.household = r.household
            WHERE hm.user.id = :userId
            GROUP BY r.shop
            """)
    Set<ShopOccurrence> getShopOccurrences(final long userId);
}
