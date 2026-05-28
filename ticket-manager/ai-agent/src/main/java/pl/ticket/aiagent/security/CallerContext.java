package pl.ticket.aiagent.security;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;

public record CallerContext(
        String subject,
        Set<String> scopes,
        Set<String> roles
) {

    private static final String ANONYMOUS_SUBJECT = "anonymous";

    public CallerContext {
        subject = StringUtils.hasText(subject) ? subject : ANONYMOUS_SUBJECT;
        scopes = scopes == null ? Set.of() : Set.copyOf(scopes);
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public static CallerContext anonymous() {
        return new CallerContext(ANONYMOUS_SUBJECT, Set.of(), Set.of());
    }

    public boolean hasAllScopes(Collection<String> requiredScopes) {
        if (requiredScopes == null || requiredScopes.isEmpty()) {
            return true;
        }
        return scopes.containsAll(requiredScopes);
    }
}
