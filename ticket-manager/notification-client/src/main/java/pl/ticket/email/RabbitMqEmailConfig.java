package pl.ticket.email;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMqEmailConfig
{
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    /*Queues*/
    @Value("${rabbitmq.email-queue.email}")
    private String emailQueue;

    /*Routing keys*/
    @Value("${rabbitmq.email-routing-keys.internal-email}")
    private String emailRoutingKey;

}
