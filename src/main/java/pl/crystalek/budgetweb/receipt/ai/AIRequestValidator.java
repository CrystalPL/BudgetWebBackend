package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.receipt.ai.model.AIRequestValidationResult;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AIRequestValidator {
    AIProperties aiProperties;

    public AIRequestValidationResult validate(final MultipartFile file) {
        final String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
            return AIRequestValidationResult.INVALID_FILE_TYPE;
        }

        final String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (fileExtension == null || !aiProperties.getAllowedPhotoExtensions().contains(fileExtension)) {
            return AIRequestValidationResult.INVALID_FILE_EXTENSION;
        }

        if (file.getSize() > aiProperties.getMaxPhotoSize().toBytes()) {
            return AIRequestValidationResult.FILE_SIZE_EXCEEDED;
        }

        return AIRequestValidationResult.SUCCESS;
    }
}
