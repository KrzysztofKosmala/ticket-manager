package pl.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
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
