package pl.crystalek.budgetweb.user.avatar;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.avatar.response.UploadAvatarResponseMessage;
import pl.crystalek.budgetweb.user.model.User;

import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class UploadAvatar {
    EntityManager entityManager;
    AvatarRepository avatarRepository;

    ResponseAPI<UploadAvatarResponseMessage> uploadAvatar(final long userId, final MultipartFile file) {
        avatarRepository.findByUser_Id(userId).ifPresent(this::deleteCurrentAvatarIfExists);
        entityManager.flush();

        final Avatar avatar = saveAvatar(userId, file);

        final boolean movingResult = moveAvatarToDirectory(file, avatar);
        if (!movingResult) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.FILE_UPLOAD_ERROR);
        }

        return new ResponseAPI<>(true, UploadAvatarResponseMessage.SUCCESS);
    }

    private void deleteCurrentAvatarIfExists(final Avatar avatar) {
        new File(AvatarFacade.AVATAR_DIRECTORY, avatar.getId().toString() + "." + avatar.getExtension()).delete();
        avatarRepository.delete(avatar);
    }

    private Avatar saveAvatar(final long userId, final MultipartFile file) {
        final User userReference = entityManager.getReference(User.class, userId);
        final String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        final Avatar avatar = new Avatar(userReference, fileExtension);

        return avatarRepository.save(avatar);
    }

    private boolean moveAvatarToDirectory(final MultipartFile imageFile, final Avatar avatar) {
        try {
            imageFile.transferTo(new File(AvatarFacade.AVATAR_DIRECTORY, avatar.getId().toString() + "." + avatar.getExtension()).toPath());
        } catch (final IOException exception) {
            return false;
        }

        return true;
    }
}
