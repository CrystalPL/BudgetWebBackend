package pl.crystalek.budgetweb.user.profile.avatar;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
@FieldDefaults(makeFinal = true, level = AccessLevel.PACKAGE)
class AvatarUtils {
    File AVATAR_DIRECTORY = new File(System.getProperty("user.dir") + "/avatars");
    String DEFAULT_AVATAR_FILE_NAME = "default.jpg";

    static {
        if (!AVATAR_DIRECTORY.exists()) {
            AVATAR_DIRECTORY.mkdir();
        }
    }

    File getAvatarFile(final String fileName) {
        final File file = new File(AVATAR_DIRECTORY, fileName);
        if (!file.exists()) {
            return new File(AVATAR_DIRECTORY, DEFAULT_AVATAR_FILE_NAME);
        }

        return file;
    }
}
