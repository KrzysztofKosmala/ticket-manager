package pl.ticket.aiagent.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CallerContextProvider {

    private static final String SCOPE_PREFIX = "SCOPE_";
    private static final String ROLE_PREFIX = "ROLE_";

    public CallerContext current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return CallerContext.anonymous();
        }

        return new CallerContext(
                authentication.getName(),
                authoritiesWithPrefix(authentication, SCOPE_PREFIX),
                authoritiesWithPrefix(authentication, ROLE_PREFIX)
        );
    }

    private Set<String> authoritiesWithPrefix(Authentication authentication, String prefix) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(prefix))
                .map(authority -> authority.substring(prefix.length()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
