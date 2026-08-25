package pl.ticket.aitoolsgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import pl.ticket.aitoolsgateway.event.EventToolHandler;
import pl.ticket.aitoolsgateway.orders.OrdersToolHandler;
import pl.ticket.aitoolsgateway.service.AiToolsGatewayService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ToolConfigTest {

    @Test
    void shouldExposeGatewayServiceToolsAsSpringAiToolCallbackProvider() {
        AiToolsGatewayService service = new AiToolsGatewayService(
                mock(OrdersToolHandler.class),
                mock(EventToolHandler.class)
        );
        ToolConfig config = new ToolConfig();

        ToolCallbackProvider provider = config.gatewayToolCallbackProvider(service);

        assertThat(provider.getToolCallbacks())
                .extracting(callback -> callback.getToolDefinition().name())
                .containsExactlyInAnyOrder(
                        "tm_my_orders_search",
                        "tm_events_search"
                );
    }
}
