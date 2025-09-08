package pl.crystalek.budgetweb.token;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.token.model.RefreshToken;
import pl.crystalek.budgetweb.user.model.User;

interface TokenRepository extends CrudRepository<RefreshToken, Long> {

    @Modifying
    @Query("DELETE FROM RefreshToken WHERE user.id = :userId AND rememberMe = true")
    void deleteByUser_IdAndRememberMeIsTrue(final long userId);

    void deleteByUser(final User user);
}
