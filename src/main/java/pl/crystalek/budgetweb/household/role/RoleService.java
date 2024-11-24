package pl.crystalek.budgetweb.household.role;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.household.Household;

import java.time.Instant;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RoleService {
    RoleRepository repository;

    public Role createDefaultRole(final Household household) {
        final Role role = new Role(household, "Domownik", "#17d12a", Instant.now());

        return repository.save(role);
    }

    public Role createOwnerDefaultRole(final Household household) {
        final Role role = new Role(household, "Założyciel", "#d11717", Instant.now());

        return repository.save(role);
    }
}
