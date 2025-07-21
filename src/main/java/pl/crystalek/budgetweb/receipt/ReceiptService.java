package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.receipt.ai.AIReceiptService;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptResponse;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptRequest;
import pl.crystalek.budgetweb.receipt.response.CreateReceiptDetailsResponse;
import pl.crystalek.budgetweb.receipt.response.DeleteReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.GetReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.ShopOccurrence;
import pl.crystalek.budgetweb.receipt.response.UserWhoPaid;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReceiptService {
    ReceiptRepository repository;
    UserService userService;
    AIReceiptService aiReceiptService;

    public CompletableFuture<AIReceiptResponse> loadByAI(final MultipartFile file, final long userId) {
        final User user = userService.getUserById(userId).get();
        return aiReceiptService.sendRequest(file, user);
    }

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

        repository.save(receipt);

        return verifyReceiptResponse;
    }

    public ResponseAPI<DeleteReceiptResponse> deleteReceipt(final String stringReceiptId, final long requesterId) {
        if (stringReceiptId == null || stringReceiptId.isBlank()) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.MISSING_RECEIPT_ID);
        }

        final long receiptId;
        try {
            receiptId = Long.parseLong(stringReceiptId);
        } catch (final NumberFormatException exception) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.ERROR_NUMBER_FORMAT);
        }

        final Optional<Receipt> receiptOptional = repository.findById(receiptId);
        if (receiptOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.RECEIPT_NOT_FOUND);
        }

        final Receipt receipt = receiptOptional.get();
        final boolean userNotExistsInHousehold = receipt.getHousehold().getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (userNotExistsInHousehold) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.USER_NOT_FOUND_IN_HOUSEHOLD);
        }

        repository.delete(receipt);

        return new ResponseAPI<>(true, DeleteReceiptResponse.SUCCESS);
    }

    public Set<GetReceiptResponse> getReceipts(final long requesterId) {
        return repository.getReceiptsByUserId(requesterId);
    }

    public CreateReceiptDetailsResponse getCreateReceiptDetails(final long requesterId) {
        final Set<UserWhoPaid> whoPaidList = repository.getWhoPaidList(requesterId);
        final Set<ShopOccurrence> shopOccurrences = repository.getShopOccurrences(requesterId);

        return new CreateReceiptDetailsResponse(whoPaidList, shopOccurrences);
    }
}