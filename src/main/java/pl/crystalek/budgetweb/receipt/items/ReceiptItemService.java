package pl.crystalek.budgetweb.receipt.items;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.receipt.items.response.GetProductListResponse;
import pl.crystalek.budgetweb.receipt.items.response.GetReceiptItemsResponse;
import pl.crystalek.budgetweb.receipt.items.response.SuggestCategoryResponse;

import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReceiptItemService {
    ReceiptItemRepository repository;

    public SuggestCategoryResponse suggestCategory(final String productName, final long requesterId) {
        return repository.suggestCategory(productName, requesterId);
    }

    public Set<GetProductListResponse> getProductList(final long requesterId) {
        return repository.getProductList(requesterId);
    }

    public Set<GetReceiptItemsResponse> getReceiptItems(final long receiptId, final long requesterId) {
        return repository.getReceiptItemByReceiptId(receiptId, requesterId);
    }
}
