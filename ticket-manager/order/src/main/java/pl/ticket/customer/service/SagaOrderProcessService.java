package pl.ticket.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.configuration.rabbit.RabbitMqOrderConfig;
import pl.ticket.dto.OrderCreatedEvent;

@Service
@RequiredArgsConstructor
public class SagaOrderProcessService
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqOrderConfig rabbitMqOrderConfig;

    public void orderCreated(OrderCreatedEvent orderCreatedEvent)
    {
        rabbitMqMessageProducer.publish
                        (
                                orderCreatedEvent,
                                rabbitMqOrderConfig.getInternalExchange(),
                                rabbitMqOrderConfig.getInternalOrderCreatedRoutingKey()
                        );
    }
}
