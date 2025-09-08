package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptRequest;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SaveReceipt {
    UserService userService;
    ReceiptRepository receiptRepository;

    public ResponseAPI<SaveReceiptResponseMessage> saveReceipt(final SaveReceiptRequest saveReceiptRequest, final long requesterId) {
        final User requesterUser = userService.getUserById(requesterId).get();

        final ReceiptCreationValidator receiptCreationValidator = new ReceiptCreationValidator(requesterUser, saveReceiptRequest);
        final SaveReceiptResponse verifyReceiptResponse = receiptCreationValidator.verifyAll();
        if (!verifyReceiptResponse.isSuccess()) {
            return verifyReceiptResponse;
        }

        final ReceiptCreationAssembler receiptCreationAssembler = new ReceiptCreationAssembler(requesterUser, saveReceiptRequest);
        receiptCreationAssembler.createReceipt();
        final Receipt receipt = receiptCreationAssembler.getReceipt();

        receiptRepository.save(receipt);

        return verifyReceiptResponse;
    }
}
