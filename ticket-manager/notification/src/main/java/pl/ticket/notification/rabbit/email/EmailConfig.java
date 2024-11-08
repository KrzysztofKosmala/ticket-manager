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
    @Value("${rabbitmq.email-queue.email}")
    private String emailsQueue;


    /*Routing keys*/
    @Value("${rabbitmq.email-routing-keys.internal-email}")
    private String internalEmailRoutingKey;



    @Bean
    public TopicExchange internalTopicExchange()
    {
        return new TopicExchange(this.internalExchange);
    }

    /*Queues beans*/
    @Bean
    public Queue emailQueue()
    {
        return new Queue(this.emailsQueue);
    }


    /*Binding beans*/
    @Bean
    public Binding internalOrderConfirmationBinding()
    {
        return BindingBuilder.bind(emailQueue()).to(internalTopicExchange()).with(this.internalEmailRoutingKey);
    }

}
