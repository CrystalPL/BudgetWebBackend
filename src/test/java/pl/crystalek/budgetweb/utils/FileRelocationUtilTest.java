package pl.crystalek.budgetweb.utils;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@FieldDefaults(level = AccessLevel.PRIVATE)
class FileRelocationUtilTest {
    File tempFile;

    @Test
    void shouldMoveFileToTempDirectory() {
        // given
        final byte[] content = "hello-world".getBytes();
        final MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", content);
        final String prefix = "budgetweb-";

        // when
        final Optional<File> fileOptional = FileRelocationUtil.moveFileToTempDirectory(prefix, multipartFile);

        // then
        assertThat(fileOptional).isPresent();
        tempFile = fileOptional.get();
        assertThat(tempFile)
                .exists()
                .isFile();
        assertThat(tempFile.getName())
                .startsWith(prefix)
                .endsWith(".tmp");
        assertThat(tempFile).hasBinaryContent(content);
    }

    @Test
    void shouldReturnEmptyWhenTransferFails() throws IOException {
        final MultipartFile failing = mock(MultipartFile.class);
        doThrow(new IOException("IO fail")).when(failing).transferTo(any(File.class));

        final Optional<File> fileOptional = FileRelocationUtil.moveFileToTempDirectory("budgetweb-", failing);

        assertTrue(fileOptional.isEmpty());
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile.toPath());
        }
    }
}