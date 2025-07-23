package pl.crystalek.budgetweb.user.avatar;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.avatar.response.UploadAvatarResponseMessage;
import pl.crystalek.budgetweb.user.model.User;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UploadAvatarTest {

    @Mock
    EntityManager entityManager;

    @Mock
    AvatarRepository avatarRepository;

    @InjectMocks
    UploadAvatar uploadAvatar;

    User testUser;
    MultipartFile mockFile;
    long userId;
    File avatarDirectory;

    @BeforeEach
    void setUp() {
        userId = 1L;
        testUser = new User();
        mockFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "test image content".getBytes());

        // Zapisanie oryginalnej lokalizacji katalogu awatarów
        avatarDirectory = AvatarFacade.AVATAR_DIRECTORY;

        // Utworzenie tymczasowego katalogu testowego dla awatarów
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "test_avatars");
        tempDir.mkdirs();
//        AvatarFacade.AVATAR_DIRECTORY = tempDir;
    }

    @Test
    void shouldDeleteCurrentAvatarIfExistsAndUploadNewOne() throws IOException {
        // given
        UUID oldAvatarId = UUID.randomUUID();
        Avatar oldAvatar = new Avatar(testUser, "png");
        when(oldAvatar.getId()).thenReturn(oldAvatarId);
        when(oldAvatar.getExtension()).thenReturn("png");
        when(avatarRepository.findByUser_Id(userId)).thenReturn(Optional.of(oldAvatar));

        UUID newAvatarId = UUID.randomUUID();
        Avatar newAvatar = new Avatar(testUser, "jpg");
        when(newAvatar.getId()).thenReturn(newAvatarId);
        when(entityManager.getReference(User.class, userId)).thenReturn(testUser);
        when(avatarRepository.save(any(Avatar.class))).thenReturn(newAvatar);

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatar.uploadAvatar(userId, mockFile);

        // then
        verify(avatarRepository).delete(oldAvatar);
        verify(entityManager).flush();
        verify(entityManager).getReference(User.class, userId);
        verify(avatarRepository).save(any(Avatar.class));
        assertTrue(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.SUCCESS, result.getMessage());
    }

    @Test
    void shouldUploadNewAvatarWhenUserHasNoAvatar() {
        // given
        when(avatarRepository.findByUser_Id(userId)).thenReturn(Optional.empty());

        UUID newAvatarId = UUID.randomUUID();
        Avatar newAvatar = new Avatar(testUser, "jpg");
        when(newAvatar.getId()).thenReturn(newAvatarId);
        when(entityManager.getReference(User.class, userId)).thenReturn(testUser);
        when(avatarRepository.save(any(Avatar.class))).thenReturn(newAvatar);

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatar.uploadAvatar(userId, mockFile);

        // then
        verify(entityManager).getReference(User.class, userId);
        verify(avatarRepository).save(any(Avatar.class));
        assertTrue(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.SUCCESS, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileTransferFails() throws IOException {
        // given
        when(avatarRepository.findByUser_Id(userId)).thenReturn(Optional.empty());

        UUID newAvatarId = UUID.randomUUID();
        Avatar newAvatar = new Avatar(testUser, "jpg");
        when(newAvatar.getId()).thenReturn(newAvatarId);
        when(newAvatar.getExtension()).thenReturn("jpg");
        when(entityManager.getReference(User.class, userId)).thenReturn(testUser);
        when(avatarRepository.save(any(Avatar.class))).thenReturn(newAvatar);

        MultipartFile mockFileThatThrowsException = mock(MultipartFile.class);
        doThrow(new IOException("Błąd transferu pliku")).when(mockFileThatThrowsException).transferTo(any(File.class));

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatar.uploadAvatar(userId, mockFileThatThrowsException);

        // then
        verify(entityManager).getReference(User.class, userId);
        verify(avatarRepository).save(any(Avatar.class));
        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.FILE_UPLOAD_ERROR, result.getMessage());
    }

    @Test
    void shouldSaveAvatarWithCorrectProperties() {
        // given
        when(avatarRepository.findByUser_Id(userId)).thenReturn(Optional.empty());
        when(entityManager.getReference(User.class, userId)).thenReturn(testUser);

        UUID newAvatarId = UUID.randomUUID();
        Avatar savedAvatar = new Avatar(testUser, "jpg");
        when(savedAvatar.getId()).thenReturn(newAvatarId);
        when(avatarRepository.save(any(Avatar.class))).thenReturn(savedAvatar);

        // when
        uploadAvatar.uploadAvatar(userId, mockFile);

        // then
        // Sprawdzenie, czy avatar został zapisany z odpowiednimi parametrami
        verify(avatarRepository).save(any(Avatar.class));
    }
}
