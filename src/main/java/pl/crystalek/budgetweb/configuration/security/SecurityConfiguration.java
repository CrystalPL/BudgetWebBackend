package pl.crystalek.budgetweb.configuration.security;

import jakarta.servlet.DispatcherType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.crystalek.budgetweb.configuration.handler.CustomAccessDeniedHandler;
import pl.crystalek.budgetweb.configuration.handler.CustomAuthenticationEntryPoint;
import pl.crystalek.budgetweb.user.CustomUserDetailsService;

@EnableScheduling
@EnableAsync
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SecurityConfiguration {
    CustomUserDetailsService customUserDetailsService;
    PasswordEncoder passwordEncoder;
    CustomAccessDeniedHandler customAccessDeniedHandler;
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain applicationSecurity(final HttpSecurity httpSecurity, final AuthenticationFilter authenticationFilter) throws Exception {
        httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //TODO ZWRACANIE NOT FOUND GDY PÓJDZIE REQUEST DO NIEISTNIEJĄCEGO PUNKTU KOŃCOWEGO
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(registry -> registry
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        .requestMatchers("/household/invitations/invite").authenticated()
                        .requestMatchers("/auth/confirm").permitAll()
                        .requestMatchers("/auth/login").anonymous()
                        .requestMatchers("/auth/register").anonymous()
                        .requestMatchers("/auth/password/recovery").anonymous()
                        .requestMatchers("/auth/password/reset").permitAll()
                        .requestMatchers("/account/confirm-change-email/**").permitAll()
                        .requestMatchers("/auth/resend-email").anonymous()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
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
