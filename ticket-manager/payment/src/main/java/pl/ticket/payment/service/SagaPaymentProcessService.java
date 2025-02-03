package pl.ticket.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.amqp.RabbitMqMessageProducer;
import pl.ticket.payment.configuration.rabbit.RabbitMqConfig;
import pl.ticket.dto.OrderEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaPaymentProcessService
{
    private final RabbitMqMessageProducer rabbitMqMessageProducer;
    private final RabbitMqConfig rabbitMqConfig;

    public void publishPaymentCompleted(OrderEvent orderEvent)
    {
        log.trace("Publishing event to payment completed, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                        (
                                orderEvent,
                                rabbitMqConfig.getInternalExchange(),
                                rabbitMqConfig.getInternalPaymentCompletedRoutingKey()
                        );
    }
    public void publishPaymentRejected(OrderEvent orderEvent)
    {
        log.trace("Publishing event to payment rejected, event: {}", orderEvent.toString());
        rabbitMqMessageProducer.publish
                (
                        orderEvent,
                        rabbitMqConfig.getInternalExchange(),
                        rabbitMqConfig.getInternalPaymentRejectedRoutingKey()

                );

    }

}
