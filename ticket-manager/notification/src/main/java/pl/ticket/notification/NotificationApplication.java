package pl.ticket.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


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
//    private JavaMailSender mailSender;
//    @Value("${spring.mail.username}")
//    private String projectMail;

//    public NotificationApplication(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo("pnq.priv@gmail.com");
//        simpleMailMessage.setSubject("Test app");
//        simpleMailMessage.setText("Test app ticket manager!");
//        simpleMailMessage.setFrom(projectMail);
//        mailSender.send(simpleMailMessage);
//    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class);
    }
}
