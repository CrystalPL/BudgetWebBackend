package pl.crystalek.budgetweb.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@UtilityClass
public class FileRelocationUtil {
    public Optional<File> moveFileToTempDirectory(final String prefix, final MultipartFile file) {
        try {
            final File tempFile = File.createTempFile(prefix, ".tmp");
            file.transferTo(tempFile);

            return Optional.of(tempFile);
        } catch (final IOException exception) {
            log.error(exception.getMessage(), exception);
            return Optional.empty();
        }
    }
}
