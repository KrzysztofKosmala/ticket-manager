package pl.ticket.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.configuration.rabbit.RabbitMqOrderConfig;
import pl.ticket.dto.OrderEvent;

@Service
@RequiredArgsConstructor
public class SagaOrderProcessService
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqOrderConfig rabbitMqOrderConfig;

    public void orderCreated(OrderEvent orderEvent)
    {
        rabbitMqMessageProducer.publish
                        (
                                orderEvent,
                                rabbitMqOrderConfig.getInternalExchange(),
                                rabbitMqOrderConfig.getInternalOrderCreatedRoutingKey()
                        );
    }
}
