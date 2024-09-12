package pl.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
//probowalem w ten sposob ale latwiej by bylo ze zwyklym
@EnableReactiveFeignClients
        (
                basePackages = "pl.ticket.feign"
        )
public class OrderApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(OrderApplication.class,args);
    }
}