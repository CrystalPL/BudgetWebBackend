package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptResponse;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptResponseMessage;
import pl.crystalek.budgetweb.receipt.ai.model.ReceiptAnalysisResult;
import pl.crystalek.budgetweb.receipt.properties.ReceiptProperties;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.utils.FileRelocationUtil;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AIReceiptService {
    AIReceiptRequestValidator requestValidator;
    ReceiptDocumentAnalyzer documentAnalyzer;
    ReceiptCategoryAssigner categoryAssigner;
    ReceiptProperties receiptProperties;
    UserService userService;

    public CompletableFuture<AIReceiptResponse> sendRequest(final MultipartFile multipartFile, final long userId) {
        final AIReceiptResponseMessage validateResult = requestValidator.validate(multipartFile);
        if (validateResult != AIReceiptResponseMessage.SUCCESS) {
            return CompletableFuture.completedFuture(new AIReceiptResponse(false, validateResult));
        }

        final Optional<File> imageFileOptional = FileRelocationUtil.moveFileToTempDirectory("receipt-", multipartFile);
        if (imageFileOptional.isEmpty()) {
            return CompletableFuture.completedFuture(new AIReceiptResponse(false, AIReceiptResponseMessage.IMAGE_PROCESSING_ERROR));
        }

        final User requesterUser = userService.getUserById(userId).get();
        return processReceipt(imageFileOptional.get(), requesterUser);
    }

    @Async
    protected CompletableFuture<AIReceiptResponse> processReceipt(final File imageFile, final User requesterUser) {
        try {
            final Set<Category> categories = requesterUser.getHouseholdMember().getHousehold().getCategories();
            final ReceiptAnalysisResult analysis = documentAnalyzer.analyze(imageFile);
            final AIProcessedReceipt categorizedReceipt = categoryAssigner.assign(analysis.receipt(), analysis.rawText(), categories);

            final AIReceipt aiReceipt = new AIReceiptMapper(categorizedReceipt, receiptProperties, requesterUser).map();
            return CompletableFuture.completedFuture(new AIReceiptResponse(true, AIReceiptResponseMessage.SUCCESS, aiReceipt));
        } catch (final Exception exception) {
            log.error("Przetwarzanie paragonu nie powiodło się", exception);
            return CompletableFuture.completedFuture(new AIReceiptResponse(false, AIReceiptResponseMessage.FAILED_TO_RECEIPT_PROCESS));
        }
    }
}
