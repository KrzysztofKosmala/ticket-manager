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
    @Value("${rabbitmq.order-exchanges.internal}")
    private String internalExchange;

    /*Queues*/
    @Value("${rabbitmq.order-queue.orderCreated}")
    private String orderCreatedQueue;
    @Value("${rabbitmq.order-queue.orderReserved}")
    private String orderReservedQueue;
    @Value("${rabbitmq.order-queue.orderChangeStatus}")
    private String orderChangeStatusQueue;

    /*Routing keys*/
    @Value("${rabbitmq.order-routing-keys.internal-orderCreated}")
    private String internalOrderCreatedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-orderReserved}")
    private String internalOrderReservedRoutingKey;
    @Value("${rabbitmq.order-routing-keys.internal-orderChangeStatus}")
    private String internalOrderChangeStatusRoutingKey;


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


    /*Binding beans*/
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
        return BindingBuilder.bind(orderCreatedQueue()).to(internalTopicExchange()).with(this.internalOrderReservedRoutingKey);
    }

}
