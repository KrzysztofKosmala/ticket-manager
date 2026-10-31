package pl.ticket.aitoolsgateway.event;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static org.assertj.core.api.Assertions.assertThat;

class EventToolHandlerConfigurationTest {

    @Test
    void fakeEventHandlerShouldBeTheDefaultImplementation() {
        ConditionalOnProperty condition = FakeEventToolHandler.class.getAnnotation(ConditionalOnProperty.class);

        assertThat(condition.name())
                .containsExactly("ai-tools-gateway.tools.event.handler");
        assertThat(condition.havingValue())
                .isEqualTo("fake");
        assertThat(condition.matchIfMissing())
                .isTrue();
    }

    @Test
    void realEventHandlerShouldBeEnabledOnlyByExplicitProperty() {
        ConditionalOnProperty condition = RealEventToolHandler.class.getAnnotation(ConditionalOnProperty.class);

        assertThat(condition.name())
                .containsExactly("ai-tools-gateway.tools.event.handler");
        assertThat(condition.havingValue())
                .isEqualTo("real");
        assertThat(condition.matchIfMissing())
                .isFalse();
    }
}
