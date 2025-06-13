package pl.crystalek.budgetweb.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.crystalek.budgetweb.user.model.UserCredentialsDTO;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomUserDetailsService implements UserDetailsService {
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userService.getUserCredentials(username)
                .map(this::userDetailsMapper)
                .orElseThrow(this::throwException);
    }

    private UserDetails userDetailsMapper(final UserCredentialsDTO user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.email())
                .password(user.password())
                .build();
    }

    private UsernameNotFoundException throwException() {
        return new UsernameNotFoundException("User does not exist");
    }
}
