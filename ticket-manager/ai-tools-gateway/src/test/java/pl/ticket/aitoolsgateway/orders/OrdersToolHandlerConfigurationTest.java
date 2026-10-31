package pl.ticket.aitoolsgateway.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static org.assertj.core.api.Assertions.assertThat;

class OrdersToolHandlerConfigurationTest {

    @Test
    void fakeOrdersHandlerShouldBeTheDefaultImplementation() {
        ConditionalOnProperty condition = FakeOrdersToolHandler.class.getAnnotation(ConditionalOnProperty.class);

        assertThat(condition.name())
                .containsExactly("ai-tools-gateway.tools.orders.handler");
        assertThat(condition.havingValue())
                .isEqualTo("fake");
        assertThat(condition.matchIfMissing())
                .isTrue();
    }

    @Test
    void realOrdersHandlerShouldBeEnabledOnlyByExplicitProperty() {
        ConditionalOnProperty condition = RealOrdersToolHandler.class.getAnnotation(ConditionalOnProperty.class);

        assertThat(condition.name())
                .containsExactly("ai-tools-gateway.tools.orders.handler");
        assertThat(condition.havingValue())
                .isEqualTo("real");
        assertThat(condition.matchIfMissing())
                .isFalse();
    }
}
