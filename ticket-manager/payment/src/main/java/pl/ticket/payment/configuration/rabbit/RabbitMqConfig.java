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
public class RabbitMqConfig
{
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;
    /*Queues*/
    @Value("${rabbitmq.order-queue.paymentInitialized}")
    private String paymentInitialized;
    @Value("${rabbitmq.order-queue.paymentCompleted}")
    private String paymentCompleted;
    @Value("${rabbitmq.order-queue.paymentRejected}")
    private String paymentRejected;
    @Value("${rabbitmq.order-queue.orderReserved}")
    private String orderReserved;
    @Value("${rabbitmq.email-queue.email}")
    private String emailQueue;
    @Value("${rabbitmq.refound-queue.refoundPayment}")
    private String refoundQueue;

    /*Routing keys*/
    @Value("${rabbitmq.order-routing-keys.internal-paymentInitialized}")
    private String internalPaymentInitializedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-paymentCompleted}")
    private String internalPaymentCompletedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-paymentRejected}")
    private String internalPaymentRejectedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-orderReserved}")
    private String internalOrderReservedRoutingKey;
    @Value("${rabbitmq.email-routing-keys.internal-email}")
    private String emailRoutingKey;
    @Value("${rabbitmq.refound-routing-keys.internal-refoundPayment}")
    private String internalRefoundPaymentRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange()
    {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue refoundQueue(){return new Queue(this.refoundQueue);}

    @Bean
    public Binding internalRefoundBinding()
    {
        return BindingBuilder.bind(refoundQueue()).to(internalTopicExchange()).with(this.internalRefoundPaymentRoutingKey);
    }
}
