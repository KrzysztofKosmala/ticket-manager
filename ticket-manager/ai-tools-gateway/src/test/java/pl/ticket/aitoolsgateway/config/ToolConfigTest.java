package pl.ticket.aitoolsgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import pl.ticket.aitoolsgateway.service.AiToolsGatewayService;
import pl.ticket.aitoolsgateway.service.SmokeToolService;
import pl.ticket.feign.order.OrderClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ToolConfigTest {

    @Test
    void shouldExposeGatewayServiceToolsAsSpringAiToolCallbackProvider() {
        AiToolsGatewayService service = new AiToolsGatewayService(
                mock(OrderClient.class),
                new SmokeToolService()
        );
        ToolConfig config = new ToolConfig();

        ToolCallbackProvider provider = config.gatewayToolCallbackProvider(service);

        assertThat(provider.getToolCallbacks())
                .extracting(callback -> callback.getToolDefinition().name())
                .contains("tm_orders_search", "tm_smoke_ping");
    }
}
