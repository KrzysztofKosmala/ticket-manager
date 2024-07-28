package pl.ticket.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication(

        scanBasePackages = {
                "pl.ticket.notification",
                "pl.ticket.amqp",
                "pl.ticket.feign"
        }
)
@EnableFeignClients(
        basePackages = "pl.ticket.feign"
)
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class);
    }
}