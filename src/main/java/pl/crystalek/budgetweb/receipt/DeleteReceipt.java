package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.receipt.response.DeleteReceiptResponse;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.utils.NumberUtil;

import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class DeleteReceipt {
    ReceiptRepository receiptRepository;

    ResponseAPI<DeleteReceiptResponse> deleteReceipt(final String stringReceiptId, final long requesterId) {
        if (stringReceiptId == null || stringReceiptId.isBlank()) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.MISSING_RECEIPT_ID);
        }

        final Optional<Long> receiptIdOptional = NumberUtil.getLong(stringReceiptId);
        if (receiptIdOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.ERROR_NUMBER_FORMAT);
        }

        final long receiptId = receiptIdOptional.get();
        final Optional<Receipt> receiptOptional = receiptRepository.findById(receiptId);
        if (receiptOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.RECEIPT_NOT_FOUND);
        }

        final Receipt receipt = receiptOptional.get();
        final boolean userNotExistsInHousehold = receipt.getHousehold().getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (userNotExistsInHousehold) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.USER_NOT_FOUND_IN_HOUSEHOLD);
        }

        receiptRepository.delete(receipt);

        return new ResponseAPI<>(true, DeleteReceiptResponse.SUCCESS);
    }
}

