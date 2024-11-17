package pl.ticket.event.internal.ticket.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.dto.CompleteOrderEvent;
import pl.ticket.dto.OrderEvent;
import pl.ticket.event.configuration.rabbit.RabbitMqOrderConfig;


@Service
@RequiredArgsConstructor
@Slf4j
public class SagaReservationProcessService
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqOrderConfig rabbitMqOrderConfig;

    public void publishReservationCompleted(OrderEvent orderEvent)
    {
        log.info("publishing event to reservation completed, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getReservationCompletedRoutingKey()
                );
    }

    public void publishReservationRejected(OrderEvent orderEvent)
    {
        log.info("publishing event to reservation rejected, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getReservationRejectedRoutingKey()
                );
    }

    public void publishOrderUnbooked(OrderEvent order)
    {
        log.info("publishing event to order unbooked, event: {}", order.toString());
        rabbitMqMessageProducer.publish
                (
                        order,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getOrderUnbookedRoutingKey()
                );
    }
    public void publishPreparedConcreteTickets(CompleteOrderEvent completeOrderEvent)
    {
        log.info("publishing event to PreparedConcreteTickets, event: {}", completeOrderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        completeOrderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getPreparedConcreteTicketsRoutingKey()
                );
    }
}