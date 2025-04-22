package pl.crystalek.budgetweb.auth.controller.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;

@Getter
public class RegisterResponse extends ResponseAPI<RegisterResponseMessage> {
    @JsonIgnore
    private TemporaryUser createdUser;

    public RegisterResponse(final boolean success, final RegisterResponseMessage message, final TemporaryUser createdUser) {
        super(success, message);

        this.createdUser = createdUser;
    }

    public RegisterResponse(final boolean success, final RegisterResponseMessage message) {
        super(success, message);
    }
}
