package pl.ticket.aiagent.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CallerContextProviderTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAnonymousContextWhenUserIsNotAuthenticated() {
        CallerContextProvider provider = new CallerContextProvider();

        CallerContext context = provider.current();

        assertThat(context.subject()).isEqualTo("anonymous");
        assertThat(context.scopes()).isEmpty();
        assertThat(context.roles()).isEmpty();
    }

    @Test
    void shouldReturnAnonymousContextForSpringAnonymousAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken(
                        "anonymous-key",
                        "anonymousUser",
                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                )
        );
        CallerContextProvider provider = new CallerContextProvider();

        CallerContext context = provider.current();

        assertThat(context.subject()).isEqualTo("anonymous");
        assertThat(context.scopes()).isEmpty();
        assertThat(context.roles()).isEmpty();
    }

    @Test
    void shouldExtractSubjectScopesAndRolesFromSpringSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user-123",
                        "n/a",
                        List.of(
                                new SimpleGrantedAuthority("SCOPE_tools:orders.read"),
                                new SimpleGrantedAuthority("ROLE_CUSTOMER")
                        )
                )
        );
        CallerContextProvider provider = new CallerContextProvider();

        CallerContext context = provider.current();

        assertThat(context.subject()).isEqualTo("user-123");
        assertThat(context.scopes()).containsExactly("tools:orders.read");
        assertThat(context.roles()).containsExactly("CUSTOMER");
    }
}
