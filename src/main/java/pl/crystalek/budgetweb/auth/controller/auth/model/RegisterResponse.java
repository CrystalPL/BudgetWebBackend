package pl.crystalek.budgetweb.auth.controller.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.User;

@Getter
public class RegisterResponse extends ResponseAPI<RegisterResponseMessage> {
    @JsonIgnore
    private User createdUser;

    public RegisterResponse(final boolean success, final RegisterResponseMessage message, final User createdUser) {
        super(success, message);

        this.createdUser = createdUser;
    }

    public RegisterResponse(final boolean success, final RegisterResponseMessage message) {
        super(success, message);
    }
}
