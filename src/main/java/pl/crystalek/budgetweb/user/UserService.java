package pl.crystalek.budgetweb.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.auth.controller.auth.model.RegisterRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<String> getEmailByUserId(final long userId) {
        return userRepository.findEmailById(userId);
    }

    public User createUser(final RegisterRequest registerRequest) {
        final User user = new User(registerRequest.email(), passwordEncoder.encode(registerRequest.password()), registerRequest.username(), registerRequest.receiveUpdates());
        return userRepository.save(user);
    }

    public boolean isUserExists(final String email) {
        return userRepository.existsByEmail(email);
    }


}
