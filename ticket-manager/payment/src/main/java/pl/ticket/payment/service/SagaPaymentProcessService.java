package pl.ticket.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.dto.EmailMessage;
import pl.ticket.payment.configuration.rabbit.RabbitMqOrderConfig;
import pl.ticket.dto.OrderEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaPaymentProcessService
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqOrderConfig rabbitMqOrderConfig;

    public void publishPaymentCompleted(OrderEvent orderEvent)
    {
        log.info("publishing event to order created, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                        (
                                orderEvent,
                                rabbitMqOrderConfig.getInternalExchange(),
                                rabbitMqOrderConfig.getInternalPaymentCompletedRoutingKey()
                        );
    }
    public void publishPaymentRejected(OrderEvent orderEvent)
    {
        log.info("publishing event to order created, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqOrderConfig.getInternalExchange(),
                        rabbitMqOrderConfig.getInternalOrderReservedRoutingKey()

                );

    }

}
