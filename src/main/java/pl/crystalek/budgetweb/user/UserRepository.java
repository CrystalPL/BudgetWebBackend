package pl.crystalek.budgetweb.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.user.model.AccountInfoResponse;
import pl.crystalek.budgetweb.user.model.UserCredentialsDTO;
import pl.crystalek.budgetweb.user.model.UserDTO;

import java.util.Optional;

interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    Optional<UserCredentialsDTO> findUserCredentialsByEmail(final String email);

    Optional<UserDTO> findUserDTOByEmail(final String email);

    @Query("SELECT new pl.crystalek.budgetweb.user.model.AccountInfoResponse(u.nickname, u.email, u.id) " +
           "FROM User u WHERE u.id = :userId")
    AccountInfoResponse findAccountInfoById(final long userId);

    @Query("SELECT u.email FROM User u WHERE u.id = :id")
    Optional<String> findEmailById(final long id);

    boolean existsByEmail(final String email);

    @Query("SELECT u.password FROM User u WHERE u.id = :id")
    Optional<String> findPasswordById(final long id);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :id")
    void updatePasswordById(final long id, final String newPassword);

    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickname WHERE u.id = :id")
    void updateUsernameById(final long id, final String nickname);
}