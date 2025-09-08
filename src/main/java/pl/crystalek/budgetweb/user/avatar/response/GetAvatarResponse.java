package pl.crystalek.budgetweb.user.avatar.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.io.File;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAvatarResponse extends ResponseAPI<GetAvatarResponseMessage> {
    File avatar;

    public GetAvatarResponse(final boolean success, final GetAvatarResponseMessage message) {
        super(success, message);
    }

    public GetAvatarResponse(final boolean success, final GetAvatarResponseMessage message, final File avatar) {
        super(success, message);

        this.avatar = avatar;
    }
}
