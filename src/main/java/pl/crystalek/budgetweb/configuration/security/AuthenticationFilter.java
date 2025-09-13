package pl.crystalek.budgetweb.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.crystalek.budgetweb.auth.CookieService;
import pl.crystalek.budgetweb.household.role.permission.Permission;
import pl.crystalek.budgetweb.household.role.permission.RolePermissionService;
import pl.crystalek.budgetweb.token.TokenFacade;
import pl.crystalek.budgetweb.token.model.AccessTokenDetails;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AuthenticationFilter extends OncePerRequestFilter {
    TokenFacade tokenFacade;
    CookieService cookieService;
    RolePermissionService rolePermissionService;
    Set<String> shouldNotFilter = Set.of("/auth/confirm", "/auth/password/recovery", "/auth/password/reset", "/account/confirm-change-email");

    private static void anonymousUser(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("anonymous", "anonymous", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        filterChain.doFilter(request, response);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        System.out.println(request.getRequestURI());
        final Optional<Cookie> cookieOptional = cookieService.getCookieWithToken(request.getCookies());
        if (cookieOptional.isEmpty()) {
            anonymousUser(request, response, filterChain);
            return;
        }

        final Cookie cookie = cookieOptional.get();
        final String cookieValue = cookie.getValue();
        final AccessTokenDetails tokenDetails = tokenFacade.decodeToken(cookieValue);
        if (!tokenDetails.isVerified()) {
            anonymousUser(request, response, filterChain);
            return;
        }

        if (tokenDetails.isExpired()) {
            final Optional<String> newAccessTokenOptional = tokenFacade.createAccessToken(tokenDetails);
            if (newAccessTokenOptional.isEmpty()) {
                anonymousUser(request, response, filterChain);
                return;
            }

            cookieService.createCookieAndAddToResponse(newAccessTokenOptional.get(), cookie.getMaxAge() == -1, response);
        }

        final Set<Permission> userPermissions = rolePermissionService.getUserPermissions(tokenDetails.getUserId());
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tokenDetails.getUserId(), null, userPermissions);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return shouldNotFilter.stream().anyMatch(request.getRequestURI()::startsWith);
    }
}
