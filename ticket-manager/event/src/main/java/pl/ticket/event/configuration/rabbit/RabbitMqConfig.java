package pl.ticket.event.configuration.rabbit;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMqConfig
{
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    /*Queues*/
    @Value("${rabbitmq.order-queue.reservationCompleted}")
    private String reservationCompletedQueue;
    @Value("${rabbitmq.order-queue.reservationRejected}")
    private String reservationRejectedQueue;
    @Value("${rabbitmq.order-queue.orderUnbooked}")
    private String orderUnbookedQueue;
    @Value("${rabbitmq.order-queue.preparedConcreteTickets}")
    private String preparedConcreteTicketsQueue;
    @Value("${rabbitmq.refound-queue.refoundPayment}")
    private String refoundPaymentQueue;

    /*Routing keys*/
    @Value("${rabbitmq.order-routing-keys.internal-reservationCompleted}")
    private String reservationCompletedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-reservationRejected}")
    private String reservationRejectedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-orderUnbooked}")
    private String orderUnbookedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-preparedConcreteTickets}")
    private String preparedConcreteTicketsRoutingKey;
    @Value("${rabbitmq.refound-routing-keys.internal-refoundPayment}")
    private String refoundPaymentRoutingKey;

}
