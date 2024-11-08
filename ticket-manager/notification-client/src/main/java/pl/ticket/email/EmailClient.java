package pl.ticket.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.dto.EmailMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailClient
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqEmailConfig rabbitMqEmailConfig;

    public void publishEmail(EmailMessage emailMessage)
    {
        log.info("publishing message to email queue, email: {}", emailMessage.toString());
        rabbitMqMessageProducer.publish
                (
                        emailMessage,
                        rabbitMqEmailConfig.getInternalExchange(),
                        rabbitMqEmailConfig.getEmailRoutingKey()
                );
        log.info("published message to email queue, email: {}", emailMessage.toString());
    }
}
