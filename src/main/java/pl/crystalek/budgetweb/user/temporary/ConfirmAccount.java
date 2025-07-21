package pl.crystalek.budgetweb.user.temporary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.auth.response.AccountConfirmationResponseMessage;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ConfirmAccount {
    TemporaryUserRepository temporaryUserRepository;
    UserService userService;

    ResponseAPI<AccountConfirmationResponseMessage> confirmAccount(final String confirmationToken) {
        final UUID uuid = UUID.fromString(confirmationToken);
        final Optional<TemporaryUser> userOptional = temporaryUserRepository.findById(uuid);
        if (userOptional.isEmpty()) {
            return new ResponseAPI<>(false, AccountConfirmationResponseMessage.TOKEN_EXPIRED);
        }

        final TemporaryUser user = userOptional.get();
        userService.createUser(user);

        temporaryUserRepository.delete(user);
        return new ResponseAPI<>(true, AccountConfirmationResponseMessage.SUCCESS);
    }
}
