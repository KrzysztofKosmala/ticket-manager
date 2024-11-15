package pl.ticket.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.configuration.rabbit.RabbitMqOrderConfig;
import pl.ticket.dto.OrderEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrderProcessService
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqOrderConfig rabbitMqOrderConfig;

    public void publishOrderCreated(OrderEvent orderEvent)
    {
        log.info("publishing event to order created, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                        (
                                orderEvent,
                                rabbitMqOrderConfig.getInternalExchange(),
                                rabbitMqOrderConfig.getInternalOrderCreatedRoutingKey()
                        );
    }

    public void publishOrderReserved(OrderEvent orderEvent)
    {
        log.info("publishing event to order reserved, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getInternalOrderReservedRoutingKey()
                );
    }

    public void publishToUnbookOrder(OrderEvent orderEvent)
    {
        log.info("publishing event to unbook order, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getUnbookOrderRoutingKey()
                );
    }

    public void publishToPrepareConcreteTickets(OrderEvent orderEvent)
    {
        log.info("publishing event to prepareConcreteTickets, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getPrepareConcreteTicketsRoutingKey()
                );
    }
}
