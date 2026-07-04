package pl.ticket.aitoolsgateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import pl.ticket.feign.order.OrderClient;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AiToolsGatewayServiceTest {

    @Test
    void shouldExposeGatewayToolCallbacks() {
        AiToolsGatewayService service = new AiToolsGatewayService(
                mock(OrderClient.class),
                new SmokeToolService()
        );

        ToolCallbackProvider provider = toolCallbackProvider(service);

        assertThat(provider.getToolCallbacks())
                .extracting(callback -> callback.getToolDefinition().name())
                .contains("tm_orders_search")
                .contains("tm_smoke_ping");
    }

    @Test
    void shouldDescribeSmokeEchoToolInPolishForManualDiagnostics() {
        AiToolsGatewayService service = new AiToolsGatewayService(
                mock(OrderClient.class),
                new SmokeToolService()
        );
        ToolCallback callback = Arrays.stream(toolCallbackProvider(service).getToolCallbacks())
                .filter(toolCallback -> toolCallback.getToolDefinition().name().equals("tm_smoke_ping"))
                .findFirst()
                .orElseThrow();

        assertThat(callback.getToolDefinition().description())
                .contains("Uzyj tego narzedzia")
                .contains("test dymny")
                .contains("MCP")
                .contains("ai-tools-gateway")
                .contains("SMOKE_OK")
                .contains("Nie uzywaj go do prawdziwych danych");
    }

    @Test
    void shouldExecuteSmokeEchoThroughSpringAiCallback() {
        AiToolsGatewayService service = new AiToolsGatewayService(
                mock(OrderClient.class),
                new SmokeToolService()
        );
        ToolCallback callback = Arrays.stream(toolCallbackProvider(service).getToolCallbacks())
                .filter(toolCallback -> toolCallback.getToolDefinition().name().equals("tm_smoke_ping"))
                .findFirst()
                .orElseThrow();

        String result = callback.call("{}");

        assertThat(result)
                .contains("smoke")
                .contains("SMOKE_OK")
                .contains("ai-tools-gateway");
    }

    private ToolCallbackProvider toolCallbackProvider(AiToolsGatewayService service) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(service)
                .build();
    }
}
