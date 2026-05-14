package pl.ticket.aiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.ticket.aiagent.tool.AiAgentToolProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiAgentToolProperties.class)
public class AiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAgentApplication.class, args);
    }
}
