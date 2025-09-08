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
    AvatarRepository avatarRepository;

    File getAvatar(final long userId) {
        final String fileName = getImageFileName(userId);

        return AvatarUtils.getAvatarFile(fileName);
    }

    private String getImageFileName(final long userId) {
        final Optional<Avatar> byUserId = avatarRepository.findByUser_Id(userId);
        return avatarRepository.findByUser_Id(userId)
                .map(Avatar::getFileName)
                .orElse(AvatarUtils.DEFAULT_AVATAR_FILE_NAME);
    }
}
