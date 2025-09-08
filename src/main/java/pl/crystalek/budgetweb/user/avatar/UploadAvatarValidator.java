package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.avatar.response.UploadAvatarResponseMessage;

import java.util.Set;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class UploadAvatarValidator {
    static final Set<String> ALLOWED_FILE_TYPES = Set.of("image/png", "image/jpeg");
    AvatarProperties avatarProperties;

    ResponseAPI<UploadAvatarResponseMessage> validateUploadAvatarRequest(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.AVATAR_NOT_FOUND);
        }

        final String contentType = file.getContentType();
        if (StringUtils.isEmpty(contentType) || !ALLOWED_FILE_TYPES.contains(contentType)) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.INVALID_FILE_TYPE);
        }

        final String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(fileExtension) || !avatarProperties.getAllowedAvatarExtensions().contains(fileExtension)) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.INVALID_FILE_EXTENSION);
        }

        if (file.getSize() > avatarProperties.getMaxAvatarSize().toBytes()) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.FILE_SIZE_EXCEEDED);
        }

        return new ResponseAPI<>(true, UploadAvatarResponseMessage.SUCCESS);
    }
}
