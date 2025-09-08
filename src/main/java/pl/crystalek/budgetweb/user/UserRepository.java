package pl.crystalek.budgetweb.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.user.model.User;
import pl.crystalek.budgetweb.user.model.UserCredentialsDTO;
import pl.crystalek.budgetweb.user.model.UserDTO;
import pl.crystalek.budgetweb.user.response.AccountInfoResponse;

import java.util.Optional;

interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    Optional<UserCredentialsDTO> findUserCredentialsByEmail(final String email);

    Optional<UserDTO> findUserDTOByEmail(final String email);

    @Query("SELECT new pl.crystalek.budgetweb.user.response.AccountInfoResponse(u.userData.nickname, u.email, u.id) " +
           "FROM User u WHERE u.id = :userId")
    AccountInfoResponse findAccountInfoById(final long userId);

    boolean existsByEmail(final String email);

    @Query("SELECT u.password FROM User u WHERE u.id = :id")
    Optional<String> findPasswordById(final long id);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :id")
    void updatePasswordById(final long id, final String newPassword);
}