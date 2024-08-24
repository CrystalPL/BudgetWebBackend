package pl.crystalek.budgetweb.auth.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crystalek.budgetweb.auth.cookie.CookieService;
import pl.crystalek.budgetweb.auth.token.TokenDecoder;
import pl.crystalek.budgetweb.auth.token.TokenService;
import pl.crystalek.budgetweb.auth.token.model.AccessTokenDetails;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AuthenticationFilter extends OncePerRequestFilter {
    TokenDecoder tokenDecoder;
    TokenService tokenService;
    CookieService cookieService;
    Set<String> shouldNotFilter = Set.of("/auth/confirm", "/auth/password/recovery", "/auth/password/reset", "/auth/login", "/auth/register", "/h2-console");

    //https://stackoverflow.com/questions/23621037/return-http-error-401-code-skip-filter-chains
    //sendError spowoduje przekierowanie do strony obsługi błędów aplikacji i ponowne uruchomienie filtrów dla tego przekierowania
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final Optional<Cookie> cookieOptional = cookieService.getCookieWithToken(request.getCookies());
        if (cookieOptional.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("baczność kurwa");
            return;
        }

        final Cookie cookie = cookieOptional.get();
        final String cookieValue = cookie.getValue();
        final AccessTokenDetails tokenDetails = tokenDecoder.decodeToken(cookieValue);
        if (!tokenDetails.isVerified()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("baczność kurwa");
            return;
        }

        if (tokenDetails.isExpired()) {
            final Optional<String> newAccessTokenOptional = tokenService.createAccessToken(tokenDetails);
            if (newAccessTokenOptional.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                System.out.println("baczność kurwa");
                return;
            }

            cookieService.createCookieAndAddToResponse(newAccessTokenOptional.get(), cookie.getMaxAge() != -1, response);
        }

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenDetails.getUserId(), null, Set.of(tokenDetails.getRole()));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return shouldNotFilter.stream().anyMatch(request.getServletPath()::startsWith);
    }
}
