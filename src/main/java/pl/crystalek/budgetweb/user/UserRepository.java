package pl.crystalek.budgetweb.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    boolean existsByEmail(final String email);

    @Query("SELECT u.email FROM User u WHERE u.id = :id")
    Optional<String> findEmailById(final long id);

    boolean existsByIdAndPassword(final long userId, final String encodePassword);

    @Query("SELECT u.password FROM User u WHERE u.id = :id")
    Optional<String> findPasswordById(final long id);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :id")
    void updatePasswordById(final long id, final String newPassword);

    @Modifying
    @Query("UPDATE User u SET u.nickname = :nickname WHERE u.id = :id")
    void updateUsernameById(final long id, final String nickname);
}