package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import pl.crystalek.budgetweb.user.avatar.response.UploadAvatarResponseMessage;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.utils.BaseAccessControllerTest;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class AvatarControllerTest extends BaseAccessControllerTest {
    private static final File SOURCE_AVATAR_FILE = new File("src/test/resources/avatarTest/paragon1.png");

    protected String[][] shouldDeniedAccessWithoutAccount() {
        return new String[][]{{"/account/avatar", "POST"}, {"/account/avatar", "GET"}};
    }

    protected String[][] shouldAllowAccessWithAccount() {
        return new String[][]{{"/account/avatar", "POST"}, {"/account/avatar", "GET"}};
    }

    private void uploadAvatar() {
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .httpMethod(HttpMethod.POST)
                .file(SOURCE_AVATAR_FILE, MediaType.IMAGE_JPEG)
                .path("/account/avatar")
                .expectedResponseCode(HttpStatus.OK)
                .expectedResponseMessage(UploadAvatarResponseMessage.SUCCESS)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldSuccessfullyUploadAvatarFile() throws IOException {
        uploadAvatar();
        List<Avatar> result = this.entityManager.createQuery("select a from Avatar a", Avatar.class).getResultList();
        User user = this.entityManager.createQuery("select u from User u", User.class).getSingleResult();
        Avatar avatar = result.getFirst();
        File file = new File(AvatarUtils.AVATAR_DIRECTORY, avatar.getFileName());

        assertEquals(1, result.size());
        assertEquals("png", avatar.getExtension());
        assertEquals(user, avatar.getUser());
        assertTrue(FileUtils.contentEquals(file, SOURCE_AVATAR_FILE));
    }

    @Test
    void shouldReturnErrorWhenUploadingInvalidFile() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", "To nie jest prawidłowy obraz".getBytes());
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .httpMethod(HttpMethod.POST)
                .file(invalidFile)
                .path("/account/avatar")
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(UploadAvatarResponseMessage.INVALID_FILE_TYPE)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnErrorWhenUploadingOversizedFile() {
        byte[] largeContent = new byte[6291456];
        Arrays.fill(largeContent, (byte) 1);
        MockMultipartFile oversizedFile = new MockMultipartFile("file", "large_image.png", "image/png", largeContent);

        RequestHelper.builder()
                .withUser(userAccountUtil)
                .httpMethod(HttpMethod.POST)
                .file(oversizedFile)
                .path("/account/avatar")
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(UploadAvatarResponseMessage.FILE_SIZE_EXCEEDED)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnErrorWhenNoFileIsProvided() {
        RequestHelper.builder()
                .withUser(userAccountUtil)
                .httpMethod(HttpMethod.POST)
                .path("/account/avatar")
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .expectedResponseMessage(UploadAvatarResponseMessage.AVATAR_NOT_FOUND)
                .build().sendRequest(mockMvc);
    }

    @Test
    void shouldReturnUserAvatar() throws IOException {
        uploadAvatar();
        byte[] bytes = Files.readAllBytes(SOURCE_AVATAR_FILE.toPath());

        RequestHelper.builder()
                .loginUser(userAccountUtil)
                .httpMethod(HttpMethod.GET)
                .path("/account/avatar")
                .expectedResponseCode(HttpStatus.OK)
                .expect(header().string("Content-Type", MediaType.IMAGE_JPEG.toString()))
                .expect((it) -> assertArrayEquals(bytes, it.getResponse().getContentAsByteArray()))
                .build().sendRequest(mockMvc);
    }

    @AfterEach
    void tearDown() {
        List<Avatar> result = this.entityManager.createQuery("select a from Avatar a", Avatar.class).getResultList();
        if (!result.isEmpty()) {
            Avatar avatar = result.getFirst();
            new File(AvatarUtils.AVATAR_DIRECTORY, avatar.getFileName()).delete();
        }

    }
}
