package pl.crystalek.budgetweb.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterRequest;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterResponse;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.model.AccountInfoResponse;
import pl.crystalek.budgetweb.user.model.ChangeNicknameResponseMessage;
import pl.crystalek.budgetweb.user.model.ChangePasswordResponseMessage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {
    UserRepository repository;
    PasswordEncoder passwordEncoder;

    public boolean isCorrectPassword(final long userId, final String password) {
        return passwordEncoder.matches(password, repository.findPasswordById(userId).get());
    }

    public Optional<User> getUserByEmail(final String email) {
        return repository.findByEmail(email);
    }

    public Optional<String> getEmailByUserId(final long userId) {
        return repository.findEmailById(userId);
    }

    public RegisterResponse createUser(final RegisterRequest registerRequest) {
        final User user = new User(registerRequest.email(), passwordEncoder.encode(registerRequest.password()), registerRequest.username(), registerRequest.receiveUpdates());
        return new RegisterResponse(true, RegisterResponseMessage.SUCCESS, repository.save(user));
    }

    public AccountInfoResponse getAccountInfo(final long userId) {
        final User user = repository.findById(userId).get();//ignoruje optionala bo został sprawdzony w AuthenticationFilter
        return new AccountInfoResponse(user.getNickname(), user.getEmail());
    }

    public ResponseAPI<ChangePasswordResponseMessage> changePassword(final long userId, final String oldPassword, final String newPassword) {
        if (!isCorrectPassword(userId, oldPassword)) {
            return new ResponseAPI<>(false, ChangePasswordResponseMessage.BAD_CREDENTIALS);
        }

        repository.updatePasswordById(userId, passwordEncoder.encode(newPassword));
        return new ResponseAPI<>(true, ChangePasswordResponseMessage.SUCCESS);
    }

    @Transactional
    public ResponseAPI<ChangeNicknameResponseMessage> changeNickname(final long userId, final String username) {
        repository.updateUsernameById(userId, username);
        return new ResponseAPI<>(true, ChangeNicknameResponseMessage.SUCCESS);
    }

}
