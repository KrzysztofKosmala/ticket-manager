package pl.ticket.aitoolsgateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class GatewayToolCallbackStartupLoggerTest {

    @Test
    void shouldLogGatewayToolCallbackProviders(CapturedOutput output) {
        ToolCallbackProvider provider = providerReturning(
                callbackNamed("tm_orders_search"),
                callbackNamed("tm_smoke_ping")
        );
        GatewayToolCallbackStartupLogger logger = new GatewayToolCallbackStartupLogger(List.of(provider));

        logger.logToolCallbackProviders();

        assertThat(output)
                .contains("AI tools gateway callback providers:")
                .contains("tm_orders_search")
                .contains("tm_smoke_ping");
    }

    private ToolCallbackProvider providerReturning(ToolCallback... callbacks) {
        ToolCallbackProvider provider = mock(ToolCallbackProvider.class);
        when(provider.getToolCallbacks()).thenReturn(callbacks);
        return provider;
    }

    private ToolCallback callbackNamed(String name) {
        ToolDefinition toolDefinition = mock(ToolDefinition.class);
        when(toolDefinition.name()).thenReturn(name);

        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition()).thenReturn(toolDefinition);
        return callback;
    }
}
