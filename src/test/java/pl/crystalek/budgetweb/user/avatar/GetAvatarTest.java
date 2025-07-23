package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class GetAvatarTest {
    @Mock AvatarRepository avatarRepository;
    @InjectMocks GetAvatar getAvatar;
    Avatar avatar;

    @Test
    void shouldReturnDefaultAvatarWhenUserHasNoAvatar() {
        when(avatarRepository.findByUser_Id(anyLong())).thenReturn(Optional.empty());

        final File result = getAvatar.getAvatar(1);

        assertEquals(new File(AvatarFacade.AVATAR_DIRECTORY, "default.jpg"), result);
    }

    @Test
    void shouldReturnDefaultPngWhenUserAvatarFileDoesNotExist() {
        mockAvatar();

        final File result = getAvatar.getAvatar(1);

        assertEquals(new File(AvatarFacade.AVATAR_DIRECTORY, "default.jpg"), result);
    }

    @Test
    void shouldReturnUserAvatarWhenExists() throws Exception {
        mockAvatar();
        final File userAvatarFile = new File(AvatarFacade.AVATAR_DIRECTORY, avatar.getId().toString() + "." + "jpg");
        AvatarFacade.AVATAR_DIRECTORY.mkdir();
        userAvatarFile.createNewFile();

        final File result = getAvatar.getAvatar(1);

        assertEquals(userAvatarFile, result);
        userAvatarFile.delete();
    }

    private void mockAvatar() {
        final UUID uuid = UUID.randomUUID();
        avatar = mock(Avatar.class);
        when(avatar.getId()).thenReturn(uuid);
        when(avatar.getExtension()).thenReturn("jpg");
        when(avatarRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(avatar));
    }


    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(AvatarFacade.AVATAR_DIRECTORY);
    }
}
