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
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserCredentialsDTO;
import pl.crystalek.budgetweb.user.model.UserDTO;
import pl.crystalek.budgetweb.user.model.UserData;
import pl.crystalek.budgetweb.user.response.AccountInfoResponse;
import pl.crystalek.budgetweb.user.response.ChangeNicknameResponseMessage;
import pl.crystalek.budgetweb.user.response.ChangePasswordResponseMessage;
import pl.crystalek.budgetweb.user.temporary.TemporaryUser;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {
    UserRepository userRepository;
    UserDataRepository userDataRepository;
    PasswordEncoder passwordEncoder;

    public boolean isCorrectPassword(final long userId, final String password) {
        return passwordEncoder.matches(password, userRepository.findPasswordById(userId).get());
    }

    public Optional<UserCredentialsDTO> getUserCredentials(final String email) {
        return userRepository.findUserCredentialsByEmail(email);
    }

    public Optional<UserDTO> getUserDTO(final String email) {
        return userRepository.findUserDTOByEmail(email);
    }

    public boolean isUserExists(final String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(final long userId) {
        return userRepository.findById(userId);
    }

    public EnumSet<Permission> getUserHouseholdPermissions(final long userId) {
        return getUserById(userId).get().getHouseholdMember().getRole().getPermissionSet().stream()
                .map(RolePermission::getPermissionName)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Permission.class)));

    }

    public void createUser(final TemporaryUser temporaryUser) {
        final User user = userRepository.save(new User(temporaryUser.getEmail(), temporaryUser.getPassword(), temporaryUser.isReceiveUpdates()));

        userDataRepository.save(new UserData(user, temporaryUser.getNickname()));
    }

    public AccountInfoResponse getAccountInfo(final long userId) {
        return userRepository.findAccountInfoById(userId);
    }

    public ResponseAPI<ChangePasswordResponseMessage> changePassword(final long userId, final String oldPassword, final String newPassword) {
        if (!isCorrectPassword(userId, oldPassword)) {
            return new ResponseAPI<>(false, ChangePasswordResponseMessage.BAD_CREDENTIALS);
        }

        userRepository.updatePasswordById(userId, passwordEncoder.encode(newPassword));
        return new ResponseAPI<>(true, ChangePasswordResponseMessage.SUCCESS);
    }

    @Transactional
    public ResponseAPI<ChangeNicknameResponseMessage> changeNickname(final long userId, final String username) {
        userDataRepository.updateUsernameById(userId, username);
        return new ResponseAPI<>(true, ChangeNicknameResponseMessage.SUCCESS);
    }

    public Set<Long> findExistsIds(final Set<Long> ids) {
        return userRepository.findExistingIds(ids);
    }

}
