package pl.ticket.payment.configuration.rabbit;

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
public class RabbitMqOrderConfig {
    @Value("${rabbitmq.order-exchanges.internal}")
    private String internalExchange;

    /*Queues*/
    @Value("${rabbitmq.order-queue.paymentInitialized}")
    private String paymentInitialized;

    @Value("${rabbitmq.order-queue.paymentCompleted}")
    private String paymentCompleted;

    @Value("${rabbitmq.order-queue.paymentRejected}")
    private String paymentRejected;
    /*Routing keys*/
    @Value("${rabbitmq.order-routing-keys.internal-paymentInitialized}")
    private String internalPaymentInitializedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-paymentCompleted}")
    private String internalPaymentCompleteddRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-paymentRejected}")
    private String internalPaymentRejectedRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange()
    {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue paymentInitializedQueue()
    {
        return new Queue(this.paymentInitialized);
    }

    @Bean
    public Queue paymentCompletedQueue()
    {
        return new Queue(this.paymentCompleted);
    }

    @Bean
    public Queue paymentRejectedQueue()
    {
        return new Queue(this.paymentRejected);
    }
    /*Binding beans*/
    @Bean
    public Binding reservationRejectedRoutingKeyBinding()
    {
        return BindingBuilder.bind(paymentInitializedQueue()).to(internalTopicExchange()).with(this.internalPaymentInitializedRoutingKey);
    }
//    @Bean
//    public Binding reservationCompletedRoutingKeyBinding()
//    {
//        return BindingBuilder.bind(paymentCompletedQueue()).to(internalTopicExchange()).with(this.internalPaymentCompleteddRoutingKey);
//    }
//    @Bean
//    public Binding internalOrderCreatedBinding()
//    {
//        return BindingBuilder.bind(paymentRejectedQueue()).to(internalTopicExchange()).with(this.internalPaymentRejectedRoutingKey);
//    }
}
