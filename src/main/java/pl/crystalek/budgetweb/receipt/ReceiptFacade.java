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
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ReceiptFacade {
    ReceiptRepository repository;
    AIReceiptService aiReceiptService;
    SaveReceipt saveReceipt;
    DeleteReceipt deleteReceipt;

    CompletableFuture<AIReceiptResponse> loadByAI(final MultipartFile file, final long requesterId) {
        return aiReceiptService.sendRequest(file, requesterId);
    }

    ResponseAPI<SaveReceiptResponseMessage> saveReceipt(final SaveReceiptRequest saveReceiptRequest, final long requesterId) {
        return saveReceipt.saveReceipt(saveReceiptRequest, requesterId);
    }

    ResponseAPI<DeleteReceiptResponse> deleteReceipt(final String stringReceiptId, final long requesterId) {
        return deleteReceipt.deleteReceipt(stringReceiptId, requesterId);
    }

    Set<GetReceiptResponse> getReceipts(final long requesterId) {
        return repository.getReceiptsByUserId(requesterId);
    }

    CreateReceiptDetailsResponse getCreateReceiptDetails(final long requesterId) {
        final Set<UserWhoPaid> whoPaidList = repository.getWhoPaidList(requesterId);
        final Set<ShopOccurrence> shopOccurrences = repository.getShopOccurrences(requesterId);

        return new CreateReceiptDetailsResponse(whoPaidList, shopOccurrences);
    }
}