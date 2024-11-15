package pl.ticket.configuration.rabbit;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMqOrderConfig
{
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    /*Queues*/
    @Value("${rabbitmq.order-queue.orderCreated}")
    private String orderCreatedQueue;
    @Value("${rabbitmq.order-queue.orderReserved}")
    private String orderReservedQueue;
    @Value("${rabbitmq.order-queue.orderChangeStatus}")
    private String orderChangeStatusQueue;
    @Value("${rabbitmq.order-queue.reservationCompleted}")
    private String reservationCompletedQueue;
    @Value("${rabbitmq.order-queue.reservationRejected}")
    private String reservationRejectedQueue;
    @Value("${rabbitmq.order-queue.paymentCompleted}")
    private String paymentCompletedQueue;
    @Value("${rabbitmq.order-queue.prepareConcreteTickets}")
    private String prepareConcreteTicketsQueue;
    @Value("${rabbitmq.order-queue.preparedConcreteTickets}")
    private String preparedConcreteTicketsQueue;
    @Value("${rabbitmq.order-queue.paymentRejected}")
    private String paymentRejectedQueue;
    @Value("${rabbitmq.order-queue.unbookOrder}")
    private String unbookOrderQueue;
    @Value("${rabbitmq.order-queue.orderUnbooked}")
    private String orderUnbookedQueue;

    /*Routing keys*/
    @Value("${rabbitmq.order-routing-keys.internal-orderCreated}")
    private String internalOrderCreatedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-orderReserved}")
    private String internalOrderReservedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-orderChangeStatus}")
    private String internalOrderChangeStatusRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-reservationCompleted}")
    private String reservationCompletedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-reservationRejected}")
    private String reservationRejectedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-paymentCompleted}")
    private String paymentCompletedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-paymentRejected}")
    private String paymentRejectedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-prepareConcreteTickets}")
    private String prepareConcreteTicketsRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-preparedConcreteTickets}")
    private String preparedConcreteTicketsRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-unbookOrder}")
    private String unbookOrderRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-orderUnbooked}")
    private String orderUnbookedRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange()
    {
        return new TopicExchange(this.internalExchange);
    }

    /*Queues beans*/


    @Bean
    public Queue orderCreatedQueue()
    {
        return new Queue(this.orderCreatedQueue);
    }
    @Bean
    public Queue orderReservedQueue()
    {
        return new Queue(this.orderReservedQueue);
    }
    @Bean
    public Queue orderChangeStatusQueue()
    {
        return new Queue(this.orderChangeStatusQueue);
    }
    @Bean
    public Queue reservationCompletedQueue()
    {
        return new Queue(this.reservationCompletedQueue);
    }
    @Bean
    public Queue reservationRejectedQueue()
    {
        return new Queue(this.reservationRejectedQueue);
    }
    @Bean
    public Queue paymentCompletedQueue()
    {
        return new Queue(this.paymentCompletedQueue);
    }
    @Bean
    public Queue paymentRejectedQueue()
    {
        return new Queue(this.paymentRejectedQueue);
    }
    @Bean
    public Queue unbookOrderQueue()
    {
        return new Queue(this.unbookOrderQueue);
    }
    @Bean
    public Queue orderUnbookedQueue()
    {
        return new Queue(this.orderUnbookedQueue);
    }
    @Bean
    public Queue prepareConcreteTicketsQueue()
    {
        return new Queue(this.prepareConcreteTicketsQueue);
    }
    @Bean
    public Queue preparedConcreteTicketsQueue()
    {
        return new Queue(this.preparedConcreteTicketsQueue);
    }
    /*Binding beans*/


    /*Binding beans*/
    @Bean
    public Binding reservationRejectedRoutingKeyBinding()
    {
        return BindingBuilder.bind(reservationRejectedQueue()).to(internalTopicExchange()).with(this.reservationRejectedRoutingKey);
    }
    @Bean
    public Binding reservationCompletedRoutingKeyBinding()
    {
        return BindingBuilder.bind(reservationCompletedQueue()).to(internalTopicExchange()).with(this.reservationCompletedRoutingKey);
    }
    @Bean
    public Binding internalOrderCreatedBinding()
    {
        return BindingBuilder.bind(orderCreatedQueue()).to(internalTopicExchange()).with(this.internalOrderCreatedRoutingKey);
    }
    @Bean
    public Binding orderChangeStatusRoutingKeyBinding()
    {
        return BindingBuilder.bind(orderChangeStatusQueue()).to(internalTopicExchange()).with(this.internalOrderChangeStatusRoutingKey);
    }

    @Bean
    public Binding internalOrderReservedBinding()
    {
        return BindingBuilder.bind(orderReservedQueue()).to(internalTopicExchange()).with(this.internalOrderReservedRoutingKey);
    }
    @Bean
    public Binding paymentCompletedBinding()
    {
        return BindingBuilder.bind(paymentCompletedQueue()).to(internalTopicExchange()).with(this.paymentCompletedRoutingKey);
    }
    @Bean
    public Binding paymentRejectedBinding()
    {
        return BindingBuilder.bind(paymentRejectedQueue()).to(internalTopicExchange()).with(this.paymentRejectedRoutingKey);
    }
    @Bean
    public Binding unbookOrderBinding()
    {
        return BindingBuilder.bind(unbookOrderQueue()).to(internalTopicExchange()).with(this.unbookOrderRoutingKey);
    }

    @Bean
    public Binding orderUnbookedBinding()
    {
        return BindingBuilder.bind(orderUnbookedQueue()).to(internalTopicExchange()).with(this.orderUnbookedRoutingKey);
    }
    @Bean
    public Binding prepareConcreteTicketsBinding()
    {
        return BindingBuilder.bind(prepareConcreteTicketsQueue()).to(internalTopicExchange()).with(this.prepareConcreteTicketsRoutingKey);
    }
    @Bean
    public Binding preparedConcreteTicketsBinding()
    {
        return BindingBuilder.bind(preparedConcreteTicketsQueue()).to(internalTopicExchange()).with(this.preparedConcreteTicketsRoutingKey);
    }
}
