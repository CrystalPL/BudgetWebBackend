package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptItemJsonData;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptPrompt;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptResponse;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptResponseMessage;
import pl.crystalek.budgetweb.receipt.properties.ReceiptProperties;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.utils.FileRelocationUtil;
import pl.crystalek.budgetweb.utils.JsonDeserializer;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AIReceiptService {
    AIReceiptRequestValidator requestValidator;
    AIReceiptPromptBuilder promptBuilder;
    AIRequestSender requestSender;
    ReceiptProperties receiptProperties;
    UserService userService;

    public CompletableFuture<AIReceiptResponse> sendRequest(final MultipartFile multipartFile, final long userId) {
        final AIReceiptResponseMessage validateResult = requestValidator.validate(multipartFile);
        if (validateResult != AIReceiptResponseMessage.SUCCESS) {
            final AIReceiptResponse aiReceiptResponse = new AIReceiptResponse(false, validateResult);
            return CompletableFuture.completedFuture(aiReceiptResponse);
        }

        final Optional<File> imageFileOptional = FileRelocationUtil.moveFileToTempDirectory("receipt-", multipartFile);
        if (imageFileOptional.isEmpty()) {
            final AIReceiptResponse aiReceiptResponse = new AIReceiptResponse(false, AIReceiptResponseMessage.IMAGE_PROCESSING_ERROR);
            return CompletableFuture.completedFuture(aiReceiptResponse);
        }

        final User requesterUser = userService.getUserById(userId).get();
        final File imageFile = imageFileOptional.get();
        final AIReceiptPrompt prompt = getPrompt(imageFile, requesterUser);
        final CompletableFuture<String> requestResult = requestSender.sendRequest(prompt);
        return requestResult.thenApply(it -> mapToResultObject(it, requesterUser));
    }

    private AIReceiptPrompt getPrompt(final File imageFile, final User requsterUser) {
        final Set<Category> categories = requsterUser.getHouseholdMember().getHousehold().getCategories();

        return promptBuilder.buildAIMessage(imageFile, categories);
    }

    private AIReceiptResponse mapToResultObject(final String aiResponse, final User requesterUser) {
        final Optional<AIReceiptItemJsonData> jsonPurchaseDataOptional = mapJsonToModel(aiResponse);
        if (jsonPurchaseDataOptional.isEmpty()) {
            return new AIReceiptResponse(false, AIReceiptResponseMessage.FAILED_TO_RECEIPT_PROCESS);
        }

        final AIProcessedReceiptMapper aiProcessedReceiptMapper = new AIProcessedReceiptMapper(jsonPurchaseDataOptional.get());
        final AIProcessedReceipt aiProcessedReceipt = aiProcessedReceiptMapper.map();
        final AIReceiptMapper aiReceiptMapper = new AIReceiptMapper(aiProcessedReceipt, receiptProperties, requesterUser);
        final AIReceipt aiReceipt = aiReceiptMapper.map();

        return new AIReceiptResponse(true, AIReceiptResponseMessage.SUCCESS, aiReceipt);
    }

    private Optional<AIReceiptItemJsonData> mapJsonToModel(final String aiResponse) {
        final String json = clearAiResponse(aiResponse);
        return JsonDeserializer.deserializeJson(json, AIReceiptItemJsonData.class);
    }

    private String clearAiResponse(final String aiResponse) {
        return aiResponse
                .replace("json", "")
                .replace("```", "");
    }
}
