package pl.crystalek.budgetweb.auth.controller.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import pl.crystalek.budgetweb.share.ResponseAPI;

import java.util.UUID;

@Getter
public class LoginResponse extends ResponseAPI<LoginResponseMessage> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID registrationToken;

    public LoginResponse(final boolean success, final LoginResponseMessage message, final UUID registrationToken) {
        super(success, message);

        this.registrationToken = registrationToken;
    }

    public LoginResponse(final boolean success, final LoginResponseMessage message) {
        super(success, message);
    }
}
