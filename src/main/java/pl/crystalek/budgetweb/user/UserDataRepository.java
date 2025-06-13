package pl.crystalek.budgetweb.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.crystalek.budgetweb.user.model.UserData;

interface UserDataRepository extends CrudRepository<UserData, Long> {

    @Modifying
    @Query("UPDATE UserData u SET u.nickname = :nickname WHERE u.id = :id")
    void updateUsernameById(final long id, final String nickname);
}
