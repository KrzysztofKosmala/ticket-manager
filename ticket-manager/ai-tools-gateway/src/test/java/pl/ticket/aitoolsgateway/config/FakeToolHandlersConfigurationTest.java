package pl.ticket.aitoolsgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pl.ticket.aitoolsgateway.cart.FakeCartToolHandler;
import pl.ticket.aitoolsgateway.event.FakeEventToolHandler;
import pl.ticket.aitoolsgateway.payment.FakePaymentToolHandler;

import static org.assertj.core.api.Assertions.assertThat;

class FakeToolHandlersConfigurationTest {

    @Test
    void cartFakeHandlerShouldBeDefaultImplementation() {
        assertFakeDefault(FakeCartToolHandler.class.getAnnotation(ConditionalOnProperty.class),
                "ai-tools-gateway.tools.cart.handler");
    }

    @Test
    void paymentFakeHandlerShouldBeDefaultImplementation() {
        assertFakeDefault(FakePaymentToolHandler.class.getAnnotation(ConditionalOnProperty.class),
                "ai-tools-gateway.tools.payment.handler");
    }

    @Test
    void eventFakeHandlerShouldBeDefaultImplementation() {
        assertFakeDefault(FakeEventToolHandler.class.getAnnotation(ConditionalOnProperty.class),
                "ai-tools-gateway.tools.event.handler");
    }

    private void assertFakeDefault(ConditionalOnProperty condition, String propertyName) {
        assertThat(condition.name()).containsExactly(propertyName);
        assertThat(condition.havingValue()).isEqualTo("fake");
        assertThat(condition.matchIfMissing()).isTrue();
    }
}
