package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.auth.request.RegisterRequest;
import pl.crystalek.budgetweb.auth.response.RegisterResponse;
import pl.crystalek.budgetweb.auth.response.RegisterResponseMessage;

import java.time.Instant;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class CreateUser {
    AccountConfirmationProperties accountConfirmationProperties;
    PasswordEncoder passwordEncoder;
    TemporaryUserRepository temporaryUserRepository;

    RegisterResponse createUser(final RegisterRequest registerRequest) {
        final Instant emailExpireTime = Instant.now().plus(accountConfirmationProperties.getEmailExpireTime());

        final TemporaryUser user = new TemporaryUser(registerRequest.email(), passwordEncoder.encode(registerRequest.password()), registerRequest.username(), registerRequest.receiveUpdates(), emailExpireTime);
        return new RegisterResponse(true, RegisterResponseMessage.SUCCESS, temporaryUserRepository.save(user));
    }
}
