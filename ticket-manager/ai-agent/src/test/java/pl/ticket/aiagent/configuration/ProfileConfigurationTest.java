package pl.ticket.aiagent.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;
import pl.ticket.aiagent.configuration.security.SecurityConfig;
import pl.ticket.aiagent.tools.NoopToolCandidateSelector;
import pl.ticket.aiagent.tools.StaticToolCandidateSelector;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileConfigurationTest {

    @Test
    void staticToolCandidateSelectorShouldBeEnabledOnlyForLocalAndTestProfiles() {
        Profile profile = StaticToolCandidateSelector.class.getAnnotation(Profile.class);

        assertThat(profile.value())
                .containsExactly("local", "test");
    }

    @Test
    void noopToolCandidateSelectorShouldBeUsedOutsideLocalAndTestProfiles() {
        Profile profile = NoopToolCandidateSelector.class.getAnnotation(Profile.class);

        assertThat(profile.value())
                .containsExactly("!local & !test");
    }

    @Test
    void securityConfigurationShouldNotDefineSmokeProfileOverrides() {
        assertThat(Arrays.stream(SecurityConfig.class.getDeclaredMethods())
                .map(Method::getName))
                .doesNotContain("smokeSecurityWebFilterChain");

        assertThat(Arrays.stream(SecurityConfig.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(Profile.class))
                .filter(profile -> profile != null)
                .flatMap(profile -> Arrays.stream(profile.value())))
                .noneMatch(value -> value.contains("smoke"));
    }
}
