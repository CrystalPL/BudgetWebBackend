package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.avatar.response.UploadAvatarResponseMessage;

import java.io.File;

@RestController
@RequestMapping("/account/avatar")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AvatarController {
    AvatarService avatarService;

    @PostMapping()
    public ResponseEntity<ResponseAPI<UploadAvatarResponseMessage>> uploadAvatar(
            @RequestBody(required = false) final MultipartFile file, @AuthenticationPrincipal final long userId
    ) {
        if (file == null || file.isEmpty()) {
            throw new HttpMessageNotReadableException("Avatar not found");
        }

        final ResponseAPI<UploadAvatarResponseMessage> response = avatarService.uploadAvatar(userId, file);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<FileSystemResource> getAvatar(@AuthenticationPrincipal final long userId) {
        final File avatar = avatarService.getAvatar(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");

        return new ResponseEntity<>(new FileSystemResource(avatar), headers, HttpStatus.OK);
    }
}
