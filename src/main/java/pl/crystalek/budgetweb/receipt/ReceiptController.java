package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.receipt.items.ReceiptItemService;
import pl.crystalek.budgetweb.receipt.items.response.GetProductListResponse;
import pl.crystalek.budgetweb.receipt.items.response.GetReceiptItemsResponse;
import pl.crystalek.budgetweb.receipt.items.response.SuggestCategoryResponse;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptRequest;
import pl.crystalek.budgetweb.receipt.response.CreateReceiptDetailsResponse;
import pl.crystalek.budgetweb.receipt.response.DeleteReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.GetReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.Set;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ReceiptController {
    ReceiptService receiptService;
    ReceiptItemService receiptItemService;

    @PostMapping("/loadByAI")
    public void loadReceiptByAI(@RequestBody final MultipartFile file, @AuthenticationPrincipal final long userId) {
        receiptService.loadByAI(file, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseAPI<DeleteReceiptResponse>> deleteReceipt(@PathVariable final String id, @AuthenticationPrincipal final long userId) {
        final ResponseAPI<DeleteReceiptResponse> response = receiptService.deleteReceipt(id, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseAPI<SaveReceiptResponseMessage>> saveReceipt(
            @AuthenticationPrincipal final long userId,
            @Validated @RequestBody final SaveReceiptRequest saveReceiptRequest
    ) {
        final ResponseAPI<SaveReceiptResponseMessage> response = receiptService.saveReceipt(saveReceiptRequest, userId);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public Set<GetReceiptResponse> getReceipt(@AuthenticationPrincipal final long userId) {
        return receiptService.getReceipts(userId);
    }

    @GetMapping("/getCreateDetails")
    public CreateReceiptDetailsResponse getCreateReceiptDetails(@AuthenticationPrincipal final long userId) {
        return receiptService.getCreateReceiptDetails(userId);
    }

    @GetMapping("/getProductList")
    public Set<GetProductListResponse> getProductList(@AuthenticationPrincipal final long userId) {
        return receiptItemService.getProductList(userId);
    }

    @GetMapping("/suggestCategory/{productName}")
    public SuggestCategoryResponse suggestCategory(
            @PathVariable final String productName,
            @AuthenticationPrincipal final long userId
    ) {
        return receiptItemService.suggestCategory(productName, userId);
    }

    @GetMapping("/items/{receiptId}")
    public Set<GetReceiptItemsResponse> getReceiptItems(@PathVariable final long receiptId, @AuthenticationPrincipal final long userId) {
        return receiptItemService.getReceiptItems(receiptId, userId);
    }
}
