package pl.ticket.event.internal.ticket.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.ticket.amqp.RabbitMQConfig;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.dto.OrderCreatedEvent;

@Component
@Slf4j
public class TicketListener
{

        RabbitMqMessageProducer rabbitMqMessageProducer;
        RabbitMQConfig rabbitMQConfig;

        @RabbitListener(queues = "${rabbitmq.order-queue.orderCreated}")
        public void handleTicketReservation(OrderCreatedEvent orderCreatedEvent)
        {
                log.info("Received event to update ticket amount, event: {}", orderCreatedEvent.toString());

        }
}
