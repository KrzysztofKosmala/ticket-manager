package pl.ticket.event.internal.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.dto.OrderEvent;
import pl.ticket.event.configuration.rabbit.RabbitMqOrderConfig;


@Service
@RequiredArgsConstructor
public class SagaReservationProcessService
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqOrderConfig rabbitMqOrderConfig;

    public void publishReservationCompleted(OrderEvent orderEvent)
    {
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getReservationCompletedRoutingKey()
                );
    }

    public void publishReservationRejected(OrderEvent orderEvent)
    {
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getReservationRejectedRoutingKey()
                );
    }
}