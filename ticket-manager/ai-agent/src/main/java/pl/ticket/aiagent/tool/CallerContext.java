package pl.ticket.aiagent.tool;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public record CallerContext(String subject, Set<String> scopes) {

    public static CallerContext from(Jwt jwt) {
        if (jwt == null) {
            return new CallerContext("anonymous", Set.of());
        }
        Set<String> scopes = new HashSet<>();
        Object scope = jwt.getClaims().get("scope");
        if (scope instanceof String scopeValue && !scopeValue.isBlank()) {
            scopes.addAll(Arrays.asList(scopeValue.split(" ")));
        }
        Object scp = jwt.getClaims().get("scp");
        if (scp instanceof Collection<?> scpValues) {
            scpValues.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(scopes::add);
        }
        return new CallerContext(jwt.getSubject(), Set.copyOf(scopes));
    }
}
