package pl.crystalek.budgetweb.user.avatar;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.avatar.response.UploadAvatarResponseMessage;
import pl.crystalek.budgetweb.user.model.User;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AvatarService {
    static File AVATAR_DIRECTORY = new File(System.getProperty("user.dir") + "/avatars");

    AvatarRepository repository;
    EntityManager entityManager;
    AvatarProperties avatarProperties;

    @Transactional
    public ResponseAPI<UploadAvatarResponseMessage> uploadAvatar(final long userId, final MultipartFile file) {
        final String contentType = file.getContentType();
        if (!contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.INVALID_FILE_TYPE);
        }

        final String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (fileExtension == null || !avatarProperties.getAllowedAvatarExtensions().contains(fileExtension)) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.INVALID_FILE_EXTENSION);
        }

        if (file.getSize() > avatarProperties.getMaxAvatarSize().toBytes()) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.FILE_SIZE_EXCEEDED);
        }

        final User userReference = entityManager.getReference(User.class, userId);
        final Optional<Avatar> avatarOptional = repository.findByUser_Id(userId);
        if (avatarOptional.isPresent()) {
            final Avatar avatar = avatarOptional.get();
            new File(AVATAR_DIRECTORY, avatar.getId().toString() + "." + avatar.getExtension()).delete();
            repository.delete(avatar);
        }

        entityManager.flush();
        final Avatar avatar = repository.save(new Avatar(userReference, fileExtension));

        try {
            file.transferTo(new File(AVATAR_DIRECTORY, avatar.getId().toString() + "." + fileExtension).toPath());
        } catch (final IOException exception) {
            return new ResponseAPI<>(false, UploadAvatarResponseMessage.FILE_UPLOAD_ERROR);
        }

        return new ResponseAPI<>(true, UploadAvatarResponseMessage.SUCCESS);
    }

    public File getAvatar(final long userId) {
        final Optional<Avatar> avatarOptional = repository.findByUser_Id(userId);
        final String fileName;
        if (avatarOptional.isPresent()) {
            final Avatar avatar = avatarOptional.get();
            fileName = avatar.getId().toString() + "." + avatar.getExtension();
        } else {
            fileName = "default.jpg";
        }

        final File file = new File(AVATAR_DIRECTORY, fileName);
        if (!file.exists()) {
            return new File(AVATAR_DIRECTORY, "default.png");
        }

        return file;
    }
}
