package pl.crystalek.budgetweb.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@UtilityClass
public class FileRelocationUtil {
    public Optional<File> moveFileToTempDirectory(final String prefix, final MultipartFile file) {
        try {
            final File tempFile = File.createTempFile(prefix, ".tmp");
            file.transferTo(tempFile);

            return Optional.of(tempFile);
        } catch (final IOException exception) {
            return Optional.empty();
        }
    }
}
