package pl.crystalek.budgetweb.auth.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.crystalek.budgetweb.user.CustomUserDetailsService;
import pl.crystalek.budgetweb.user.UserRole;

@EnableAsync
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SecurityConfiguration {
    CustomUserDetailsService customUserDetailsService;
    PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain applicationSecurity(final HttpSecurity httpSecurity, final AuthenticationFilter authenticationFilter) throws Exception {
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/auth/confirm").permitAll()
//                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/password/**").permitAll()
                        .requestMatchers("/auth/resend-email").hasRole(UserRole.GUEST.name())
//                        .anyRequest().authenticated()
                        .anyRequest().hasAnyRole(UserRole.USER.name())
                )
                .build();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider impl = new DaoAuthenticationProvider(passwordEncoder);
        impl.setUserDetailsService(customUserDetailsService);
        impl.setHideUserNotFoundExceptions(false);
        return impl;
    }

}