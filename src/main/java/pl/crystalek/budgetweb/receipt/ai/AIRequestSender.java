package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.receipt.ai.model.result.AIRequestResult;
import pl.crystalek.budgetweb.receipt.ai.model.result.AIRequestResultMessage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AIRequestSender {
    AIProperties aiProperties;
    ChatModel chatModel;

    @Async
    public CompletableFuture<AIRequestResult> sendRequest(final MultipartFile file) {
        final Optional<File> movedFileOptional = moveFileToTempDirectory(file);
        if (movedFileOptional.isEmpty()) {
            return CompletableFuture.completedFuture(new AIRequestResult(null, AIRequestResultMessage.IMAGE_PROCESSING_ERROR));
        }

        final File movedFile = movedFileOptional.get();
        final Message userMessage = prepareChatMessage(movedFile);
        final String result = chatModel.call(userMessage);

        return CompletableFuture.completedFuture(new AIRequestResult(result, AIRequestResultMessage.SUCCESS));
    }

    private Optional<File> moveFileToTempDirectory(final MultipartFile file) {
        try {
            final File tempFile = File.createTempFile("receipt-", ".tmp");
            file.transferTo(tempFile);

            return Optional.of(tempFile);
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    private Message prepareChatMessage(final File movedFile) {
        final String fileExtension = FilenameUtils.getExtension(movedFile.getName());
        final MimeType mimeType = fileExtension.contains("png") ? MimeTypeUtils.IMAGE_PNG : MimeTypeUtils.IMAGE_JPEG;
        final Media media = new Media(mimeType, new FileSystemResource(movedFile));

        return new UserMessage(aiProperties.getPrompt(), media);
    }
}
