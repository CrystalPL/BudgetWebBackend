package pl.crystalek.budgetweb.user.avatar;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    AvatarFacade avatarFacade;

    @PostMapping()
    public ResponseEntity<ResponseAPI<UploadAvatarResponseMessage>> uploadAvatar(
            @RequestBody(required = false) final MultipartFile file,
            @AuthenticationPrincipal final long userId
    ) {
        final ResponseAPI<UploadAvatarResponseMessage> response = avatarFacade.uploadAvatar(userId, file);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<FileSystemResource> getAvatar(@AuthenticationPrincipal final long userId) {
        final File avatar = avatarFacade.getAvatar(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");

        return new ResponseEntity<>(new FileSystemResource(avatar), headers, HttpStatus.OK);
    }

//    @GetMapping
//    public ResponseEntity<FileSystemResource> getAvatar(
//            @PathVariable final String targetUserId,
//            @AuthenticationPrincipal final long requesterUserId
//    ) {
//        final File avatar = avatarFacade.getAvatar(requesterUserId);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
//
//        return new ResponseEntity<>(new FileSystemResource(avatar), headers, HttpStatus.OK);
//    }
}
