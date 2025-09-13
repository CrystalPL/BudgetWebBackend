package pl.crystalek.budgetweb.user.profile.email;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

interface ChangeEmailRepository extends CrudRepository<ChangeEmail, UUID> {

    Optional<ChangeEmail> findByConfirmationToken_User_Id(final long userId);

    boolean existsByNewEmailAndConfirmationToken_User_IdNot(final String email, final long userId);

    boolean existsByConfirmationToken_User_Id(final long userId);

    boolean existsByNewEmail(final String email);
}
