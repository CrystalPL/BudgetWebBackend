package pl.crystalek.budgetweb.user.temporary;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

interface TemporaryUserRepository extends CrudRepository<TemporaryUser, UUID> {

    boolean existsByEmail(final String email);

    Optional<TemporaryUser> findByEmail(final String email);

    @Query("SELECT tu.id FROM TemporaryUser tu WHERE tu.email = :email")
    Optional<UUID> findIdByEmail(final String email);
}
