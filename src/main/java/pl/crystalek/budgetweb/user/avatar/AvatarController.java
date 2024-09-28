package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.avatar.model.UploadAvatarResponseMessage;

import java.io.File;

@RestController
@RequestMapping("/account/avatar")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AvatarController {
    AvatarService avatarService;

    @PostMapping()
    private ResponseEntity<ResponseAPI<UploadAvatarResponseMessage>> uploadAvatar(@RequestBody final MultipartFile file) {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ResponseAPI<UploadAvatarResponseMessage> response = avatarService.uploadAvatar(userId, file);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    private ResponseEntity<FileSystemResource> getAvatar() {
        final long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final File avatar = avatarService.getAvatar(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");

        return new ResponseEntity<>(new FileSystemResource(avatar), headers, HttpStatus.OK);
    }
}
