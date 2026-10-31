package pl.ticket.aiagent.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import pl.ticket.aiagent.configuration.security.SecurityConfig;
import pl.ticket.aiagent.service.tools.NoopToolCandidateSelector;
import pl.ticket.aiagent.service.tools.StaticToolCandidateSelector;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ToolSelectorConfigurationTest {

    @Test
    void staticToolCandidateSelectorShouldBeEnabledByExplicitProperty() {
        ConditionalOnProperty condition = StaticToolCandidateSelector.class.getAnnotation(ConditionalOnProperty.class);

        assertThat(condition.name())
                .containsExactly("ai-agent.tools.selection-mode");
        assertThat(condition.havingValue())
                .isEqualTo("static");
        assertThat(condition.matchIfMissing())
                .isFalse();
    }

    @Test
    void noopToolCandidateSelectorShouldBeDefaultWhenPropertyIsMissing() {
        ConditionalOnProperty condition = NoopToolCandidateSelector.class.getAnnotation(ConditionalOnProperty.class);

        assertThat(condition.name())
                .containsExactly("ai-agent.tools.selection-mode");
        assertThat(condition.havingValue())
                .isEqualTo("none");
        assertThat(condition.matchIfMissing())
                .isTrue();
    }

    @Test
    void securityConfigurationShouldNotDefineProfileOverrides() {
        assertThat(Arrays.stream(SecurityConfig.class.getDeclaredMethods())
                .map(Method::getName))
                .doesNotContain("profileSpecificSecurityWebFilterChain");

        assertThat(Arrays.stream(SecurityConfig.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(Profile.class))
                .filter(profile -> profile != null)
                .flatMap(profile -> Arrays.stream(profile.value())))
                .isEmpty();
    }
}
