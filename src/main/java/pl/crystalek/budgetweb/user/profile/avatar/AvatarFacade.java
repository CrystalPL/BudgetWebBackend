package pl.crystalek.budgetweb.user.profile.avatar;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.profile.avatar.response.UploadAvatarResponseMessage;

import java.io.File;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class AvatarFacade {
    UploadAvatar uploadAvatar;
    UploadAvatarValidator uploadAvatarValidator;
    GetAvatar getAvatar;

    @Transactional
    public ResponseAPI<UploadAvatarResponseMessage> uploadAvatar(final long userId, final MultipartFile file) {
        final ResponseAPI<UploadAvatarResponseMessage> validateResult = uploadAvatarValidator.validateUploadAvatarRequest(file);
        if (!validateResult.isSuccess()) {
            return validateResult;
        }

        return uploadAvatar.uploadAvatar(userId, file);
    }

    public File getAvatar(final long userId) {
        return getAvatar.getAvatar(userId);
    }

    public File getAvatar(final String targetUserId, final long requesterUserId) {

        return getAvatar.getAvatar(requesterUserId);
    }
}
