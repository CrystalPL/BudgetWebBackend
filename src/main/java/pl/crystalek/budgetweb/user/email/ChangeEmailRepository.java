package pl.crystalek.budgetweb.user.email;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChangeEmailRepository extends CrudRepository<ChangeEmail, UUID> {

    Optional<ChangeEmail> findByConfirmationToken_Id(final UUID id);

    Optional<ChangeEmail> findByConfirmationToken_User_Id(final long userId);

    boolean existsByNewEmailAndConfirmationToken_User_IdNot(final String email, final long userId);
}
