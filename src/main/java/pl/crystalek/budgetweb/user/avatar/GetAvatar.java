package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class GetAvatar {
    private static final String DEFAULT_AVATAR_FILE_NAME = "default.jpg";
    AvatarRepository avatarRepository;

    File getAvatar(final long userId) {
        final String fileName = getImageFileName(userId);

        final File file = new File(AvatarFacade.AVATAR_DIRECTORY, fileName);
        if (!file.exists()) {
            return new File(AvatarFacade.AVATAR_DIRECTORY, DEFAULT_AVATAR_FILE_NAME);
        }

        return file;
    }

    private String getImageFileName(final long userId) {
        final Optional<Avatar> avatarOptional = avatarRepository.findByUser_Id(userId);
        if (avatarOptional.isEmpty()) {
            return DEFAULT_AVATAR_FILE_NAME;
        }

        final Avatar avatar = avatarOptional.get();
        return avatar.getId().toString() + "." + avatar.getExtension();
    }
}
