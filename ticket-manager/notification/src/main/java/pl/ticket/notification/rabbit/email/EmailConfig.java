package pl.ticket.notification.rabbit.email;
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
public class EmailConfig
{
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    /*Queues*/
    @Value("${rabbitmq.queue.orderConfirmation}")
    private String orderConfirmationQueue;
    @Value("${rabbitmq.queue.accountConfirmation}")
    private String accountConfirmationQueue;
    @Value("${rabbitmq.queue.resetPassword}")
    private String resetPasswordQueue;

    /*Routing keys*/
    @Value("${rabbitmq.routing-keys.internal-accountConfirmation}")
    private String internalAccountConfirmationRoutingKey;
    @Value("${rabbitmq.routing-keys.internal-orderConfirmation}")
    private String internalOrderConfirmationRoutingKey;
    @Value("${rabbitmq.routing-keys.internal-resetPassword}")
    private String internalResetPasswordRoutingKey;


    @Bean
    public TopicExchange internalTopicExchange()
    {
        return new TopicExchange(this.internalExchange);
    }

    /*Queues beans*/
    @Bean
    public Queue accountConfirmationQueue()
    {
        return new Queue(this.accountConfirmationQueue);
    }

    @Bean
    public Queue orderConfirmationQueue()
    {
        return new Queue(this.orderConfirmationQueue);
    }

    @Bean
    public Queue resetPasswordQueue()
    {
        return new Queue(this.resetPasswordQueue);
    }

    /*Binding beans*/
    @Bean
    public Binding internalOrderConfirmationBinding()
    {
        return BindingBuilder.bind(orderConfirmationQueue()).to(internalTopicExchange()).with(this.internalOrderConfirmationRoutingKey);
    }

    @Bean
    public Binding internalAccountConfirmationBinding()
    {
        return BindingBuilder.bind(accountConfirmationQueue()).to(internalTopicExchange()).with(this.internalAccountConfirmationRoutingKey);
    }
    @Bean
    public Binding internalResetPasswordBinding()
    {
        return BindingBuilder.bind(resetPasswordQueue()).to(internalTopicExchange()).with(this.internalResetPasswordRoutingKey);
    }
}
