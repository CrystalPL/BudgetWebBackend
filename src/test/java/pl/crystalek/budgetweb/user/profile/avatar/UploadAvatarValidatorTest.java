package pl.crystalek.budgetweb.user.profile.avatar;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.profile.avatar.response.UploadAvatarResponseMessage;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UploadAvatarValidatorTest {
    @Mock AvatarProperties avatarProperties;
    @InjectMocks UploadAvatarValidator uploadAvatarValidator;

    private static Stream<Arguments> validFilesProvider() {
        return Stream.of(
                Arguments.of("avatar.jpg", "image/jpeg", 1),
                Arguments.of("profile.png", "image/png", 10),
                Arguments.of("user-picture.jpeg", "image/jpeg", 100),
                Arguments.of("user-profile.jpg", "image/jpeg", 1024), // 1MB
                Arguments.of("small-icon.png", "image/png", 5)
        );
    }

    @Test
    void shouldReturnErrorWhenFileIsNull() {
        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(null);

        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.AVATAR_NOT_FOUND, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[0]);

        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(emptyFile);

        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.AVATAR_NOT_FOUND, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenContentTypeIsInvalid() {
        MultipartFile fileWithInvalidType = new MockMultipartFile("avatar", "avatar.jpg", "application/pdf", "test".getBytes());

        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(fileWithInvalidType);

        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.INVALID_FILE_TYPE, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileExtensionIsInvalid() {
        when(avatarProperties.getAllowedAvatarExtensions()).thenReturn(Set.of("jpg", "png", "jpeg"));
        MultipartFile fileWithInvalidExtension = new MockMultipartFile("avatar", "avatar.gif", "image/png", "test".getBytes());

        ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(fileWithInvalidExtension);

        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.INVALID_FILE_EXTENSION, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileNameHasNoExtension() {
        final MultipartFile fileWithNoExtension = new MockMultipartFile("avatar", "avatar", "image/jpeg", "test".getBytes());

        final ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(fileWithNoExtension);

        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.INVALID_FILE_EXTENSION, result.getMessage());
    }

    @Test
    void shouldReturnErrorWhenFileSizeExceedsLimit() {
        when(avatarProperties.getMaxAvatarSize()).thenReturn(DataSize.ofMegabytes(2));
        when(avatarProperties.getAllowedAvatarExtensions()).thenReturn(Set.of("jpg", "png", "jpeg"));
        final byte[] oversizedContent = new byte[1024 * 1024 * 3]; // 3MB
        final MultipartFile oversizedFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", oversizedContent);

        final ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(oversizedFile);

        assertFalse(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.FILE_SIZE_EXCEEDED, result.getMessage());
    }

    @ParameterizedTest
    @MethodSource("validFilesProvider")
    void shouldReturnSuccessForValidFiles(final String fileName, final String contentType, final int sizeInKB) {
        when(avatarProperties.getMaxAvatarSize()).thenReturn(DataSize.ofMegabytes(2));
        when(avatarProperties.getAllowedAvatarExtensions()).thenReturn(Set.of("jpg", "png", "jpeg"));
        final byte[] validContent = new byte[sizeInKB * 1024];
        final MultipartFile validFile = new MockMultipartFile("avatar", fileName, contentType, validContent);

        final ResponseAPI<UploadAvatarResponseMessage> result = uploadAvatarValidator.validateUploadAvatarRequest(validFile);

        assertTrue(result.isSuccess());
        assertEquals(UploadAvatarResponseMessage.SUCCESS, result.getMessage());
    }
}
