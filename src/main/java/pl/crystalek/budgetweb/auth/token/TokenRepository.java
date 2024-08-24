package pl.crystalek.budgetweb.auth.token;

import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.auth.token.model.RefreshToken;
import pl.crystalek.budgetweb.user.User;

interface TokenRepository extends CrudRepository<RefreshToken, Long> {

    void deleteByUserAndRememberMeIsTrue(final User user);

    void deleteByUser(final User user);
}
