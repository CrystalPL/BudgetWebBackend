package pl.crystalek.budgetweb.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crystalek.budgetweb.household.role.permission.Permission;
import pl.crystalek.budgetweb.household.role.permission.RolePermission;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.response.AccountInfoResponse;
import pl.crystalek.budgetweb.user.response.ChangeNicknameResponseMessage;
import pl.crystalek.budgetweb.user.response.ChangePasswordResponseMessage;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {
    UserRepository repository;
    PasswordEncoder passwordEncoder;

    public boolean isCorrectPassword(final long userId, final String password) {
        return passwordEncoder.matches(password, repository.findPasswordById(userId).get());
    }

    public Optional<UserCredentialsDTO> getUserCredentials(final String email) {
        return repository.findUserCredentialsByEmail(email);
    }

    public Optional<UserDTO> getUserDTO(final String email) {
        return repository.findUserDTOByEmail(email);
    }

    public boolean isUserExists(final String email) {
        return repository.existsByEmail(email);
    }

    public Optional<User> getUserByEmail(final String email) {
        return repository.findByEmail(email);
    }

    public Optional<String> getEmailByUserId(final long userId) {
        return repository.findEmailById(userId);
    }

    public Optional<User> getUserById(final long userId) {
        return repository.findById(userId);
    }

    public EnumSet<Permission> getUserHouseholdPermissions(final long userId) {
        return getUserById(userId).get().getHouseholdMember().getRole().getPermissionSet().stream()
                .map(RolePermission::getPermissionName)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Permission.class)));

    }

    public void createUser(final TemporaryUser temporaryUser) {
        final User user = new User(temporaryUser.getEmail(), temporaryUser.getPassword(), temporaryUser.getNickname(), temporaryUser.isReceiveUpdates());
        repository.save(user);
    }

    public AccountInfoResponse getAccountInfo(final long userId) {
        return repository.findAccountInfoById(userId);
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
