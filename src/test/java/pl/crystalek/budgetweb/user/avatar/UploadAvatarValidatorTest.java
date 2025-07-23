package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.avatar.response.UploadAvatarResponseMessage;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UploadAvatarValidatorTest {

    @Mock
    AvatarProperties avatarProperties;

    @InjectMocks
    UploadAvatarValidator uploadAvatarValidator;

    @BeforeEach
    void setUp() {
        when(avatarProperties.getMaxAvatarSize()).thenReturn(DataSize.ofMegabytes(2));
        when(avatarProperties.getAllowedAvatarExtensions()).thenReturn(Set.of("jpg", "png", "jpeg"));
    }

    @Test
    void shouldReturnErrorWhenFileIsNull() {
        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(null);

        // then
        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.AVATAR_NOT_FOUND, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileIsEmpty() {
        // given
        MultipartFile emptyFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[0]);

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(emptyFile);

        // then
        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.AVATAR_NOT_FOUND, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenContentTypeIsInvalid() {
        // given
        MultipartFile fileWithInvalidType = new MockMultipartFile("avatar", "avatar.jpg", "application/pdf", "test".getBytes());

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(fileWithInvalidType);

        // then
        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.INVALID_FILE_TYPE, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileExtensionIsInvalid() {
        // given
        MultipartFile fileWithInvalidExtension = new MockMultipartFile("avatar", "avatar.gif", "image/png", "test".getBytes());

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(fileWithInvalidExtension);

        // then
        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.INVALID_FILE_EXTENSION, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileSizeExceedsLimit() {
        // given
        byte[] oversizedContent = new byte[1024 * 1024 * 3]; // 3MB
        MultipartFile oversizedFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", oversizedContent);

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(oversizedFile);

        // then
        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.FILE_SIZE_EXCEEDED, result.getMessage());
    }

    @Test
    void shouldReturnSuccessForValidFile() {
        // given
        byte[] validContent = new byte[1024]; // 1KB
        MultipartFile validFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", validContent);

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(validFile);

        // then
        assertTrue(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.SUCCESS, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileNameHasNoExtension() {
        // given
        MultipartFile fileWithNoExtension = new MockMultipartFile("avatar", "avatar", "image/jpeg", "test".getBytes());

        // when
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(fileWithNoExtension);

        // then
        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.INVALID_FILE_EXTENSION, result.getMessage());
    }
}
