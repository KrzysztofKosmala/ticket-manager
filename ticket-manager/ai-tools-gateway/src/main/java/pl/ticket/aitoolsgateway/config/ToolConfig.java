package pl.ticket.aitoolsgateway.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ticket.aitoolsgateway.service.AiToolsGatewayService;

@Configuration
public class ToolConfig {

    @Bean
    public ToolCallbackProvider gatewayToolCallbackProvider(AiToolsGatewayService aiToolsGatewayService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(aiToolsGatewayService)
                .build();
    }
}
