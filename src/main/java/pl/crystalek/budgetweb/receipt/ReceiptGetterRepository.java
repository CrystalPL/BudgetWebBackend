package pl.crystalek.budgetweb.receipt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.crystalek.budgetweb.receipt.response.GetReceiptResponse;

interface ReceiptGetterRepository {

    Page<GetReceiptResponse> findReceipts(final Long userId, final Specification<Receipt> specification, final Pageable pageable);
}
