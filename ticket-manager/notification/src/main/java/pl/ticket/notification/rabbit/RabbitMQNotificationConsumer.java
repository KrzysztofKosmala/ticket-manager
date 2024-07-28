package pl.ticket.notification.rabbit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.ticket.feign.notification.EmailMessage;
import pl.ticket.notification.rabbit.email.EmailClientService;


@Component
@Slf4j
@AllArgsConstructor
public class RabbitMQNotificationConsumer
{
    private final EmailClientService emailClientService;

/*    @RabbitListener(queues = "${rabbitmq.queue.orderConfirmation}")
    public void orderConfirmationConsumer(EmailMessage message)
    {
        log.info("Consumed {} from queue", message);
        emailClientService.getInstance().send(message);
    }
    @RabbitListener(queues = "${rabbitmq.queue.resetPassword}")
    public void resetPasswordConsumer(EmailMessage message)
    {
        log.info("Consumed {} from queue", message);
        emailClientService.getInstance().send(message);
    }*/
    @RabbitListener(queues = "${rabbitmq.queue.accountConfirmation}")
    public void accountConfirmationConsumer(EmailMessage message)
    {
        log.info("Consumed {} from queue", message);
        emailClientService.getInstance().send(message);
    }
}
