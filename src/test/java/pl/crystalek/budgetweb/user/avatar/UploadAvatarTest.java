package pl.crystalek.budgetweb.user.avatar;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UploadAvatarTest {
    @Mock EntityManager entityManager;
    @Mock AvatarRepository avatarRepository;
    @Mock Avatar testAvatar;
    @Mock Avatar returnSaveAvatar;
    @InjectMocks UploadAvatar uploadAvatar;

    MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        AvatarFacade.AVATAR_DIRECTORY.mkdir();
        mockFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "test image content".getBytes());
    }

    @Test
    void shouldDeleteCurrentAvatarIfExistsAndUploadNewOne() throws IOException {
        when(avatarRepository.findByUser_Id(anyLong())).thenReturn(Optional.of(testAvatar));
        mockAvatar(testAvatar);
        mockReturnSaveAvatar();
        final File avatarFile = new File(AvatarFacade.AVATAR_DIRECTORY, testAvatar.getId().toString() + "." + testAvatar.getExtension());
        mockFile.transferTo(avatarFile);

        final ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatar.uploadAvatar(anyLong(), mockFile);

        assertFalse(avatarFile.exists());
        assertTrue(new File(AvatarFacade.AVATAR_DIRECTORY, returnSaveAvatar.getId().toString() + "." + returnSaveAvatar.getExtension()).exists());
        assertTrue(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.SUCCESS, result.getMessage());
    }

    @Test
    void shouldUploadNewAvatarWhenUserHasNoAvatar() {
        mockReturnSaveAvatar();
        final ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatar.uploadAvatar(anyLong(), mockFile);

        verify(avatarRepository, never()).delete(any());
        assertTrue(result.isSuccess());
        assertTrue(new File(AvatarFacade.AVATAR_DIRECTORY, returnSaveAvatar.getId().toString() + "." + returnSaveAvatar.getExtension()).exists());
        assertEquals(UploadAvatarResponseMessage.SUCCESS, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileTransferFails() throws IOException {
        mockReturnSaveAvatar();
        final MultipartFile mockFileThatThrowsException = mock(MultipartFile.class);
        doThrow(new IOException("Błąd transferu pliku")).when(mockFileThatThrowsException).transferTo(any(Path.class));

        final ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatar.uploadAvatar(anyLong(), mockFileThatThrowsException);

        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.FILE_UPLOAD_ERROR, result.getMessage());
    }

    @AfterEach
    void tearDown() {
        if (testAvatar != null && testAvatar.getId() != null) {
            new File(AvatarFacade.AVATAR_DIRECTORY, testAvatar.getId() + "." + testAvatar.getExtension()).delete();
        }

        if (returnSaveAvatar != null) {
            new File(AvatarFacade.AVATAR_DIRECTORY, returnSaveAvatar.getId().toString() + "." + returnSaveAvatar.getExtension()).delete();
        }

    }

    private void mockAvatar(final Avatar avatar) {
        final UUID oldAvatarId = UUID.randomUUID();
        when(avatar.getId()).thenReturn(oldAvatarId);
        when(avatar.getExtension()).thenReturn("png");
    }

    private void mockReturnSaveAvatar() {
        mockAvatar(returnSaveAvatar);
        when(avatarRepository.save(any())).thenReturn(returnSaveAvatar);
    }
}
