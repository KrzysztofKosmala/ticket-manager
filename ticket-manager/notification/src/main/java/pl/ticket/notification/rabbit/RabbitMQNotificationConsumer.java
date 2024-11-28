package pl.ticket.notification.rabbit;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.ticket.dto.EmailMessage;
import pl.ticket.notification.rabbit.email.EmailClientService;


@Component
@Slf4j
@AllArgsConstructor
public class RabbitMQNotificationConsumer
{
    private final EmailClientService emailClientService;

    @RabbitListener(queues = "${rabbitmq.email-queue.email}")
    public void emailsConsumer(EmailMessage message) throws MessagingException {
        log.info("Consumed {} from queue", message);
        emailClientService.getInstance().send(message);
    }
}
